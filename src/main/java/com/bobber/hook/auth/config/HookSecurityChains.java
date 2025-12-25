package com.bobber.hook.auth.config;

import com.bobber.hook.auth.filter.BearerHookAuthFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.bobber.event.api.EventEndpoints.INGEST_WILDCARD;
import static com.bobber.hook.api.HookEndpoints.*;

public final class HookSecurityChains {

    private HookSecurityChains() {}

    public static SecurityFilterChain hookCreateChain(HttpSecurity http)
            throws Exception {

        http
                .securityMatcher(HOOK_CREATE_API)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    public static SecurityFilterChain hookApiChain(
            HttpSecurity http,
            AuthenticationManager authManager
    ) throws Exception {

        http
                .securityMatcher(API_WILDCARD)
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationManager(authManager)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterBefore(
                        new BearerHookAuthFilter(authManager),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

