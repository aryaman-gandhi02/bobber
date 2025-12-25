package com.bobber.event.controller;

import com.bobber.event.dto.EventDetailDTO;
import com.bobber.event.dto.EventSummaryDTO;
import com.bobber.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.bobber.hook.api.HookEndpoints.API_BASE;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_BASE)
public class EventController {

    private final EventService eventService;

    @GetMapping("/hooks/{hookId}/events")
    public Page<EventSummaryDTO> listEvents(
            @PathVariable UUID hookId,
            @AuthenticationPrincipal String secret,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return eventService.listEvents(hookId, secret, pageable);
    }

    @GetMapping("/events/{eventId}")
    public EventDetailDTO getEvent(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal String secret
    ) {
        return eventService.getEvent(eventId, secret);
    }
}
