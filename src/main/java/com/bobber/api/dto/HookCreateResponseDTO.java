package com.bobber.api.dto;

import java.time.Instant;
import java.util.UUID;

public record HookCreateResponseDTO(
        UUID id,
        String secret,
        Instant createdAt
) {}

