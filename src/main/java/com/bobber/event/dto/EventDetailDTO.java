package com.bobber.event.dto;

import java.time.Instant;
import java.util.UUID;

public record EventDetailDTO(
        UUID id,
        String method,
        String path,
        String contentType,
        String headers,        // JSON string
        String queryParams,    // JSON string
        String bodyBase64,     // binary-safe
        Instant receivedAt
) {}
