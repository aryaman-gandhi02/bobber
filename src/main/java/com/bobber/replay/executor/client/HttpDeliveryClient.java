package com.bobber.replay.executor.client;

import com.bobber.domain.ReplayJob;
import com.bobber.replay.dto.HttpResult;
import com.bobber.replay.http.ReplayHeaderBuilder;
import com.bobber.replay.http.ReplayUrlBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HttpDeliveryClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final WebClient webClient;

    public HttpResult deliver(ReplayJob job) {

        byte[] body = job.getBodyOverride() != null
                ? job.getBodyOverride()
                : job.getEvent().getBody();

        return webClient
                .method(job.getEvent().getMethod())
                .uri(ReplayUrlBuilder.build(job))
                .headers(h -> ReplayHeaderBuilder.build(job, h))
                .bodyValue(body != null ? body : new byte[0])
                .exchangeToMono(response ->
                        response.bodyToMono(byte[].class)
                                .defaultIfEmpty(new byte[0])
                                .map(b -> new HttpResult(
                                        response.statusCode().value(),
                                        response.statusCode().is2xxSuccessful(),
                                        b
                                ))
                )
                .timeout(TIMEOUT)
                .block();
    }
}

