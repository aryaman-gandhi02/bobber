package com.bobber.replay.dto;

public record HttpResult(
        int status,
        boolean success,
        byte[] responseBody
) {}
