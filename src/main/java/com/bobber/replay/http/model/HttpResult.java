package com.bobber.replay.http.model;

public record HttpResult(
        int status,
        boolean success,
        byte[] responseBody
) {}
