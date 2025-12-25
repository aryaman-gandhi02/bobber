package com.bobber.replay.dto;

import com.bobber.replay.domain.ReplayJobStatus;

import java.time.Instant;
import java.util.UUID;

public record ReplayJobSummaryDTO(
        UUID id,
        ReplayJobStatus status,
        Instant createdAt,
        int attemptCount
) {
}