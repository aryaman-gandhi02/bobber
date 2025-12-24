package com.bobber.replay.dto;

import java.util.List;
import java.util.Map;

public record ReplayRequestDTO(
        String targetUrl,
        Boolean forwardAuthorization,
        Map<String, List<String>> headerOverrides,
        Map<String, List<String>> queryParamOverrides,
        String bodyOverrideBase64,
        String contentTypeOverride
) {}