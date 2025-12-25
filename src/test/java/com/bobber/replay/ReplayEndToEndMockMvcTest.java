package com.bobber.replay;

import com.bobber.event.domain.Event;
import com.bobber.event.repository.EventRepository;
import com.bobber.replay.domain.ReplayJob;
import com.bobber.replay.domain.ReplayJobStatus;
import com.bobber.replay.repository.DeliveryAttemptRepository;
import com.bobber.replay.repository.ReplayJobRepository;
import com.jayway.jsonpath.JsonPath;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReplayEndToEndMockMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ReplayJobRepository replayJobRepository;

    @Autowired
    DeliveryAttemptRepository attemptRepository;

    MockWebServer mockServer;

    @BeforeAll
    static void forceUtcTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

    @BeforeEach
    void setUp() throws Exception {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockServer.shutdown();
    }

    @Test
    void replay_reconstructs_request_correctly_via_api() throws Exception {

        // 1️⃣ Create hook
        MvcResult hookResult = mockMvc.perform(
                        post("/api/hooks")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        String hookResponse = hookResult.getResponse().getContentAsString();
        String hookId = JsonPath.read(hookResponse, "$.id");
        String hookSecret = JsonPath.read(hookResponse, "$.secret");

        // 2️⃣ Ingest event
        byte[] body = "{\"order\":\"123\"}".getBytes(StandardCharsets.UTF_8);

        mockMvc.perform(
                        post("/hook/{hookId}", hookId)
                                .queryParam("id", "123")
                                .queryParam("id", "456")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Signature", "abc")
                                .header("Authorization", "Bearer SHOULD_NOT_FORWARD")
                                .content(body)
                )
                .andExpect(status().isAccepted());

        MvcResult eventSummaryResult =  mockMvc.perform(
                                            get("/api/hooks/{hookId}/events", hookId)
                                                    .queryParam("page", "0")
                                                    .queryParam("size", "10")
                                                    .header("Authorization", "Bearer " + hookSecret)
                                            )
                                            .andExpect(status().isOk())
                                            .andReturn();
        String eventSummaryResponse = eventSummaryResult.getResponse().getContentAsString();
        List<LinkedHashMap<String, Object>> eventSummaryDTOS = JsonPath.read(eventSummaryResponse, "$.content");
        String eventId = eventSummaryDTOS.getFirst().get("id").toString();
        Event event = eventRepository.findById(UUID.fromString(eventId)).orElse(null);

        assertThat(event).isNotNull();

        // 3️⃣ Prepare replay target
        mockServer.enqueue(new MockResponse().setResponseCode(200));
        String targetUrl = mockServer.url("/").toString();

        // 4️⃣ Submit replay job
        String replayRequestJson = """
                {
                  "targetUrl": "%s",
                  "headerOverrides": {
                    "X-Signature": ["override"]
                  },
                  "queryParamOverrides": {
                    "id": ["999"]
                  },
                  "forwardAuthorization": false
                }
                """.formatted(targetUrl);

        MvcResult replayResult = mockMvc.perform(
                    post("/api/events/{eventId}/replay", event.getId())
                        .header("Authorization", "Bearer " + hookSecret)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(replayRequestJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        String replayResponse = replayResult.getResponse().getContentAsString();
        String jobId = JsonPath.read(replayResponse, "$.jobId");

        // 5️⃣ Wait briefly for async execution
        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        assertThat(attemptRepository.findAll()
                                .stream()
                                .anyMatch(attempt ->  jobId.equals(attempt.getReplayJob().getId().toString())))
                                .isEqualTo(true)
                );

        // 6️⃣ Assert outgoing request
        RecordedRequest recorded = mockServer.takeRequest();

        assertThat(recorded.getMethod()).isEqualTo("POST");
        assertThat(recorded.getPath()).isEqualTo("/hook/" + hookId + "?id=999");
        assertThat(recorded.getHeader("Content-Type")).contains("application/json");
        assertThat(recorded.getHeader("X-Signature")).isEqualTo("override");
        assertThat(recorded.getHeader("Authorization")).isNull();
        assertThat(recorded.getBody().readByteArray()).isEqualTo(body);

        // 7️⃣ Assert DB state
        ReplayJob job = replayJobRepository.findById(UUID.fromString(jobId)).orElse(null);
        assertThat(job).isNotNull();
        assertThat(job.getStatus()).isEqualTo(ReplayJobStatus.SUCCESS);
    }
}
