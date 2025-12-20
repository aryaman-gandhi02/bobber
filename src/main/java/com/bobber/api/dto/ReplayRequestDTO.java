package com.bobber.api.dto;

import java.util.Map;

public record ReplayRequestDTO(
        String targetUrl,
        Boolean forwardAuthorization,
        Map<String, String> headerOverrides,
        Map<String, String> queryParamOverrides,
        String bodyOverrideBase64,
        String contentTypeOverride
) {}