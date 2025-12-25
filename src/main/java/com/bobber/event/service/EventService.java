package com.bobber.event.service;

import com.bobber.event.domain.Event;
import com.bobber.event.dto.EventDetailDTO;
import com.bobber.event.dto.EventSummaryDTO;
import com.bobber.event.mapper.EventDetailMapper;
import com.bobber.event.repository.EventRepository;
import com.bobber.hook.domain.Hook;
import com.bobber.hook.service.HookService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final HookService hookService;
    private final EventRepository eventRepository;

    @Transactional
    public void ingestEvent(UUID hookId, HttpServletRequest request) throws IOException {
        Hook hook = hookService.requireHook(hookId);

        Event event = new Event();
        event.setHook(hook);
        event.setMethod(request.getMethod());
        event.setPath(request.getRequestURI());
        event.setReceivedAt(Instant.now());
        event.setHeaders(extractHeaders(request));
        event.setQueryParams(extractQueryParams(request));
        event.setContentType(request.getContentType());
        event.setBody(extractBody(request));

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
                        e.getMethod(),
                        e.getPath(),
                        e.getContentType(),
                        e.getReceivedAt()
                ));
    }

    public EventDetailDTO getEvent(UUID eventId, String hookSecret) {
        Event event = requireEvent(eventId, hookSecret);
        return EventDetailMapper.from(event);
    }

    public Event requireEvent(UUID eventId, String hookSecret) {
        return eventRepository.findById(eventId)
                .filter(e -> e.getHook().getSecret().equals(hookSecret))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private static Map<String, List<String>> extractHeaders(HttpServletRequest request) {
        Map<String, List<String>> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
                .forEach(name ->
                        headers.put(name, Collections.list(request.getHeaders(name)))
                );
        return headers;
    }

    private static Map<String, List<String>> extractQueryParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> List.of(e.getValue())
                ));
    }

    private static byte[] extractBody(HttpServletRequest request) throws IOException {
        byte[] body = request.getInputStream().readAllBytes();
        return body.length == 0 ? null : body;
    }

}

