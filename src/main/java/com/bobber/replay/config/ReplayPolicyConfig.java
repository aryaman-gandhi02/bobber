package com.bobber.replay.config;

import com.bobber.replay.policy.DefaultRetryPolicy;
import com.bobber.replay.policy.RetryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReplayPolicyConfig {

    @Bean
    public RetryPolicy retryPolicy() {
        return new DefaultRetryPolicy();
    }
}

