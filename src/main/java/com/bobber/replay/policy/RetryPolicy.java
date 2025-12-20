package com.bobber.replay.policy;

import java.time.Duration;

public final class RetryPolicy {

    public static final int MAX_ATTEMPTS = 3;

    private RetryPolicy() {}

    public static Duration backoff(int attemptNumber) {
        return Duration.ofSeconds(2L * attemptNumber);
    }
}

