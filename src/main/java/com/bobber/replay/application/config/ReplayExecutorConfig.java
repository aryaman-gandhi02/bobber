package com.bobber.replay.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ReplayExecutorConfig {

    @Bean(name = "executorService")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(8);
    }

}

