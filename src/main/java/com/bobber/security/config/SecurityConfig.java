package com.bobber.security.config;

import com.bobber.hook.auth.config.HookSecurityChains;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain ingestChain(HttpSecurity http) throws Exception {
        return HookSecurityChains.ingestChain(http);
    }

    @Bean
    @Order(2)
    SecurityFilterChain hookApiChain(
            HttpSecurity http,
            AuthenticationManager authManager
    ) throws Exception {
        return HookSecurityChains.hookApiChain(http, authManager);
    }
}

