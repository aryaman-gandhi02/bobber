package com.bobber.api.controller;

import com.bobber.api.dto.ReplayRequestDTO;
import com.bobber.service.ReplayJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class ReplayController {

    private final ReplayJobService replayJobService;

    @PostMapping("/{eventId}/replay")
    public Map<String, UUID> createReplayJob(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal String hookSecret,
            @RequestBody ReplayRequestDTO request
    ) {
        UUID jobId = replayJobService.createJob(eventId, hookSecret, request);
        return Map.of("jobId", jobId);
    }
}

