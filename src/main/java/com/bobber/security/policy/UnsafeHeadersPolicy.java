package com.bobber.security.policy;

import org.springframework.http.HttpHeaders;

import java.util.Set;

public final class UnsafeHeadersPolicy {

    private UnsafeHeadersPolicy() {}

    // Always unsafe, never forwardable
    private static final Set<String> ALWAYS_BLOCKED = Set.of(
            HttpHeaders.HOST,
            HttpHeaders.CONTENT_LENGTH
    );

    public static boolean isAlwaysBlocked(String headerName) {
        return ALWAYS_BLOCKED.contains(headerName.toLowerCase());
    }

    public static boolean isAuthorization(String headerName) {
        return HttpHeaders.AUTHORIZATION.equalsIgnoreCase(headerName);
    }
}
