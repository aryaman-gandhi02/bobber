package com.bobber.replay.http;

import com.bobber.event.domain.Event;
import com.bobber.replay.domain.ReplayJob;
import com.bobber.security.policy.UnsafeHeadersPolicy;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

public final class ReplayHeaderBuilder {

    private ReplayHeaderBuilder() {}

    public static void build(ReplayJob job, HttpHeaders target) {
        Event event = job.getEvent();

        copySafeHeaders(
                event.getHeaders(),
                job.isForwardAuthorization(),
                target,
                false
        );

        if (job.getHeaderOverrides() != null) {
            copySafeHeaders(
                    job.getHeaderOverrides(),
                    job.isForwardAuthorization(),
                    target,
                    true
            );
        }

        if (job.getContentTypeOverride() != null) {
            target.set(HttpHeaders.CONTENT_TYPE, job.getContentTypeOverride());
        }
    }

    /**
     * Copies headers from source → target while enforcing safety rules.
     *
     * @param override if true, existing header values are replaced
     */
    private static void copySafeHeaders(
            Map<String, List<String>> source,
            boolean forwardAuthorization,
            HttpHeaders target,
            boolean override
    ) {
        if (source == null || source.isEmpty()) {
            return;
        }

        source.forEach((rawName, values) -> {
            String normalized = UnsafeHeadersPolicy.normalize(rawName);

            if (UnsafeHeadersPolicy.isAlwaysBlocked(normalized)) {
                return;
            }

            if (UnsafeHeadersPolicy.isAuthorization(normalized) && !forwardAuthorization) {
                return;
            }

            if (override) {
                target.remove(rawName);
            }

            values.forEach(v -> target.add(rawName, v));
        });
    }
}
