package com.bobber.controller;

import com.bobber.entity.Event;
import com.bobber.entity.Hook;
import com.bobber.enums.HttpVerb;
import com.bobber.repository.EventRepository;
import com.bobber.repository.HookRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class IngestController {

    private final HookRepository hookRepository;
    private final EventRepository eventRepository;

    private final ObjectMapper objectMapper;

    @RequestMapping(value = "/hook/{hookId}", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.PATCH
    })
    public ResponseEntity<Void> ingest(
            @PathVariable UUID hookId,
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) throws IOException {

        Hook hook = hookRepository.findById(hookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Event event = new Event();
        event.setHook(hook);
        event.setMethod(HttpVerb.valueOf(request.getMethod()));
        event.setPath(request.getRequestURI());
        event.setReceivedAt(Instant.now());
        event.setBody(body);

        event.setHeaders(objectMapper.writeValueAsString(
                Collections.list(request.getHeaderNames()).stream()
                        .collect(Collectors.toMap(
                                h -> h,
                                request::getHeader
                        ))
        ));

        eventRepository.save(event);

        return ResponseEntity.ok().build();
    }
}

