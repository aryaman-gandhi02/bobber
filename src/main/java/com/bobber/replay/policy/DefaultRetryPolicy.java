package com.bobber.replay.policy;

import com.bobber.replay.domain.DeliveryAttempt;
import com.bobber.replay.domain.DeliveryAttemptStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Set;

@Slf4j
public class DefaultRetryPolicy implements RetryPolicy {

    private static final int MAX_ATTEMPTS = 3;

    private static final Set<Integer> NON_RETRYABLE_HTTP_STATUSES = Set.of(
            400, // Bad Request
            401, // Unauthorized
            403, // Forbidden
            404, // Not Found
            422  // Unprocessable Entity
    );

    @Override
    public int maxAttempts() {
        return MAX_ATTEMPTS;
    }

    @Override
    public boolean shouldRetry(DeliveryAttempt attempt) {
        if (attempt.getStatus() == DeliveryAttemptStatus.SUCCESS) {
            return false;
        }

        // If no HTTP status, this was an infra failure (timeout, DNS, etc.)
        if (attempt.getHttpStatus() == null) {
            return true;
        }

        if (NON_RETRYABLE_HTTP_STATUSES.contains(attempt.getHttpStatus())) {
            return false;
        }

        return attempt.getHttpStatus() >= 500;
    }

    @Override
    public Duration backoff(int attemptNumber) {
        // Exponential backoff with cap
        long seconds = (long) Math.min(
                Math.pow(2, attemptNumber - 1),
                30
        );

        return Duration.ofSeconds(seconds);
    }
}
