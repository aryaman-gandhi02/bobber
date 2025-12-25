package com.bobber.replay.controller;

import com.bobber.hook.auth.token.HookAuthenticationToken;
import com.bobber.replay.application.ReplayJobService;
import com.bobber.replay.dto.ReplayJobDetailDTO;
import com.bobber.replay.dto.ReplayJobSummaryDTO;
import com.bobber.replay.dto.ReplayRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
    public Map<String, UUID> createReplayJob(@PathVariable UUID eventId, @AuthenticationPrincipal String hookSecret, @RequestBody ReplayRequestDTO request) {
        UUID jobId = replayJobService.createJob(eventId, hookSecret, request);
        return Map.of("jobId", jobId);
    }

    @GetMapping("/events/{eventId}/replays")
    public Page<ReplayJobSummaryDTO> listReplays(@PathVariable UUID eventId, @AuthenticationPrincipal String hookSecret, Pageable pageable) {
        return replayJobService.listReplaysForEvent(eventId, hookSecret, pageable);
    }

    @GetMapping("/replays/{replayJobId}")
    public ReplayJobDetailDTO getReplay(
        @PathVariable UUID replayJobId,
        @AuthenticationPrincipal String secret
    ) {
        return replayJobService.getReplay(replayJobId, secret);
    }
}

