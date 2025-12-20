package com.bobber.replay.http;

import com.bobber.domain.ReplayJob;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public final class ReplayUrlBuilder {

    private ReplayUrlBuilder() {}

    public static URI build(ReplayJob job) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(job.getTargetUrl());

        // 1. Apply original query params
        applyQueryParams(builder, job.getEvent().getQueryParams());

        // 2. Apply overrides (replace semantics)
        applyQueryParams(builder, job.getQueryParamOverrides());

        return builder.build(true).toUri();
    }

    private static void applyQueryParams(
            UriComponentsBuilder builder,
            Map<String, ?> params
    ) {
        if (params == null) return;

        params.forEach((key, value) -> {
            builder.replaceQueryParam(key); // remove existing
            builder.queryParam(key, value);
        });
    }
}
