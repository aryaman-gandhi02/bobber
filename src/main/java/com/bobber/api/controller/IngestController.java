package com.bobber.api.controller;

import com.bobber.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class IngestController {

    private final EventService eventService;

    @RequestMapping(value = "/hook/{hookId}")
    public ResponseEntity<Void> ingest(@PathVariable UUID hookId, HttpServletRequest request) throws IOException {
        eventService.ingestEvent(hookId, request);
        return ResponseEntity.ok().build();
    }
}