package com.bobber.security.policy;

import java.util.Locale;
import java.util.Set;

public final class UnsafeHeadersPolicy {

    private static final Set<String> ALWAYS_BLOCKED = Set.of(
            "host",
            "content-length",
            "transfer-encoding",
            "connection"
    );

    private static final String AUTHORIZATION = "authorization";

    public static boolean isAlwaysBlocked(String header) {
        return ALWAYS_BLOCKED.contains(normalize(header));
    }

    public static boolean isAuthorization(String header) {
        return AUTHORIZATION.equals(normalize(header));
    }

    public static String normalize(String header) {
        return header.toLowerCase(Locale.ROOT);
    }
}