package com.bobber.replay.http;

import com.bobber.event.domain.Event;
import com.bobber.replay.domain.ReplayJob;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

public final class ReplayUrlBuilder {

    private ReplayUrlBuilder() {
    }

    public static URI build(ReplayJob job) {
        Event event = job.getEvent();

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(job.getTargetUrl())
                .replacePath(event.getPath())
                .replaceQuery(null);

        applyQueryParams(builder, event.getQueryParams(), false);

        applyQueryParams(builder, job.getQueryParamOverrides(), true);

        return builder.build().toUri();
    }

    /**
     * Applies query params to the builder.
     *
     * @param replace if true, existing values for the key are removed first
     */
    private static void applyQueryParams(
            UriComponentsBuilder builder,
            Map<String, List<String>> params,
            boolean replace
    ) {
        if (params == null || params.isEmpty()) return;

        params.forEach((key, values) -> {
            if (replace) {
                builder.replaceQueryParam(key);
            }
            values.forEach(v -> builder.queryParam(key, v));
        });
    }
}
