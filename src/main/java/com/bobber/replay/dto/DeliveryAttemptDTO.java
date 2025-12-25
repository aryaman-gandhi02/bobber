package com.bobber.replay.dto;

import com.bobber.replay.domain.DeliveryAttemptStatus;

import java.time.Instant;

public record DeliveryAttemptDTO(
        int attemptNumber,
        DeliveryAttemptStatus status,
        Integer httpStatus,
        Long durationMs,
        String error,
        Instant attemptedAt
) {}