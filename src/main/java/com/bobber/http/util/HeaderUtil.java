package com.bobber.http.util;

import com.bobber.security.policy.UnsafeHeadersPolicy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HeaderUtil {

    private HeaderUtil() {
    }

    public static Map<String, List<String>> sanitizeHeaders(Map<String, List<String>> headers) {
        if (headers == null) {
            return Map.of();
        }

        return headers.entrySet().stream()
                .filter(e -> !UnsafeHeadersPolicy.isAlwaysBlocked(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
