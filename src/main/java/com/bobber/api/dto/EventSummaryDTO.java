package com.bobber.api.dto;

import java.time.Instant;
import java.util.UUID;

public record EventSummaryDTO(
        UUID id,
        String method,
        String path,
        String contentType,
        Instant receivedAt
) {}

