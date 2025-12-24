package com.bobber.replay.policy;

import com.bobber.replay.domain.DeliveryAttempt;

import java.time.Duration;

public interface RetryPolicy {

    int maxAttempts();

    boolean shouldRetry(DeliveryAttempt attempt);

    Duration backoff(int attemptNumber);
}
