package com.bobber.replay.http;

import com.bobber.domain.Event;
import com.bobber.domain.ReplayJob;
import com.bobber.security.policy.UnsafeHeadersPolicy;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

public final class ReplayHeaderBuilder {

    private ReplayHeaderBuilder() {}

    public static void build(ReplayJob job, HttpHeaders target) {
        Event event = job.getEvent();

        copyHeaders(
                event.getHeaders(),
                job.isForwardAuthorization(),
                target
        );

        if (job.getHeaderOverrides() != null) {
            job.getHeaderOverrides().forEach(target::set);
        }

        if (job.getContentTypeOverride() != null) {
            target.set(HttpHeaders.CONTENT_TYPE, job.getContentTypeOverride());
        }
    }

    private static void copyHeaders(
            Map<String, List<String>> source,
            boolean forwardAuthorization,
            HttpHeaders target
    ) {
        if (source == null) return;

        source.forEach((key, values) -> {
            if (UnsafeHeadersPolicy.isAlwaysBlocked(key)) return;
            if (UnsafeHeadersPolicy.isAuthorization(key) && !forwardAuthorization) return;

            values.forEach(v -> target.add(key, v));
        });
    }
}
