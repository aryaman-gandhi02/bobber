package com.bobber.replay.controller;

import com.bobber.replay.application.ReplayJobService;
import com.bobber.replay.dto.ReplayRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static com.bobber.hook.api.HookEndpoints.API_BASE;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_BASE)
public class ReplayController {

    private final ReplayJobService replayJobService;

    @PostMapping("/events/{eventId}/replay")
    public Map<String, UUID> createReplayJob(
            @PathVariable UUID eventId,
            @AuthenticationPrincipal String hookSecret,
            @RequestBody ReplayRequestDTO request
    ) {
        UUID jobId = replayJobService.createJob(eventId, hookSecret, request);
        return Map.of("jobId", jobId);
    }
}

