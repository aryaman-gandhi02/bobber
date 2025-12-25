package com.bobber.replay.dto;

import com.bobber.replay.domain.ReplayJobStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ReplayJobDetailDTO(
        UUID id,
        ReplayJobStatus status,
        Instant createdAt,
        String targetUrl,

        Map<String, List<String>> headerOverrides,
        Map<String, List<String>> queryParamOverrides,

        long bodyOverrideSize,
        String bodyOverridePreview,
        boolean binaryBodyOverride,

        String contentTypeOverride,

        boolean forwardAuthorization
) {}
