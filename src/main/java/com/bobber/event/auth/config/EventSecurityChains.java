package com.bobber.event.auth.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static com.bobber.event.api.EventEndpoints.INGEST_WILDCARD;

public final class EventSecurityChains {

    private EventSecurityChains() {}

    public static SecurityFilterChain ingestChain(HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(INGEST_WILDCARD)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

}
