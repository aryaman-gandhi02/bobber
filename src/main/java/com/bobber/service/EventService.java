package com.bobber.service;

import com.bobber.api.dto.EventDetailDTO;
import com.bobber.api.dto.EventSummaryDTO;
import com.bobber.domain.Event;
import com.bobber.domain.Hook;
import com.bobber.domain.enums.HttpVerb;
import com.bobber.repository.EventRepository;
import com.bobber.repository.HookRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final HookRepository hookRepository;
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public void ingestEvent(UUID hookId, HttpServletRequest request) throws IOException {
        Hook hook = hookRepository.findById(hookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Event event = new Event();
        event.setHook(hook);
        event.setMethod(HttpVerb.valueOf(request.getMethod()));
        event.setPath(request.getRequestURI());
        event.setReceivedAt(Instant.now());

        Map<String, Object> headers = Collections.list(request.getHeaderNames())
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
        Hook hook = requireHook(hookId, secret);
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
        Event event = eventRepository.findById(eventId)
                .filter(e -> e.getHook().getSecret().equals(hookSecret))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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

    private Hook requireHook(UUID hookId, String secret) {
        return hookRepository.findById(hookId)
                .filter(h -> h.getSecret().equals(secret))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND)
                );
    }
}

