package com.bobber.service;

import com.bobber.api.dto.EventDetailDTO;
import com.bobber.api.dto.EventSummaryDTO;
import com.bobber.domain.Event;
import com.bobber.domain.Hook;
import com.bobber.repository.EventRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final HookService hookService;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public void ingestEvent(UUID hookId, HttpServletRequest request) throws IOException {
        Hook hook = hookService.requireHook(hookId);

        Event event = new Event();
        event.setHook(hook);
        event.setMethod(HttpMethod.valueOf(request.getMethod()));
        event.setPath(request.getRequestURI());
        event.setReceivedAt(Instant.now());

        Map<String, List<String>> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, h -> Collections.list(request.getHeaders(h))));

        event.setHeaders(headers);
        event.setContentType(request.getContentType());
        event.setQueryParams(request.getParameterMap());

        try (InputStream in = request.getInputStream()) {
            event.setBody(in.readAllBytes());
        }

        eventRepository.save(event);
    }

    public Page<EventSummaryDTO> listEvents(
            UUID hookId,
            String secret,
            Pageable pageable
    ) {
        Hook hook = hookService.requireHook(hookId, secret);
        return eventRepository
                .findByHookIdOrderByReceivedAtDesc(hook.getId(), pageable)
                .map(e -> new EventSummaryDTO(
                        e.getId(),
                        e.getMethod().name(),
                        e.getPath(),
                        e.getContentType(),
                        e.getReceivedAt()
                ));
    }

    public EventDetailDTO getEvent(UUID eventId, String hookSecret) {
        Event event = requireEvent(eventId, hookSecret);

        String bodyBase64 = event.getBody() == null
                ? null
                : Base64.getEncoder().encodeToString(event.getBody());

        return new EventDetailDTO(
                event.getId(),
                event.getMethod().name(),
                event.getPath(),
                event.getContentType(),
                objectMapper.writeValueAsString(event.getHeaders()),
                objectMapper.writeValueAsString(event.getQueryParams()),
                bodyBase64,
                event.getReceivedAt()
        );
    }

    public Event requireEvent(UUID eventId, String hookSecret) {
        return eventRepository.findById(eventId)
                .filter(e -> e.getHook().getSecret().equals(hookSecret))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

