package com.bobber.http.model;

public record HttpResult(
        int status,
        boolean success,
        byte[] responseBody
) {
}
