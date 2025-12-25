package com.bobber.event.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EventDetailDTO(
        UUID id,
        String method,
        String path,
        Map<String, List<String>> queryParams,
        Map<String, List<String>> headers,
        String contentType,
        long bodySize,
        String bodyPreview,
        boolean binaryBody,
        Instant receivedAt
) {
}