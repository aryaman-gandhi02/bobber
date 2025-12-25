package com.bobber.replay.application;

import com.bobber.replay.domain.DeliveryAttempt;
import com.bobber.replay.domain.ReplayJob;
import com.bobber.replay.http.client.HttpDeliveryClient;
import com.bobber.replay.http.model.HttpResult;
import com.bobber.replay.policy.RetryPolicy;
import com.bobber.replay.repository.DeliveryAttemptRepository;
import com.bobber.replay.repository.ReplayJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplayExecutor {

    private final ExecutorService executorService;
    private final ReplayJobRepository replayJobRepository;
    private final DeliveryAttemptRepository attemptRepository;
    private final HttpDeliveryClient httpDeliveryClient;
    private final RetryPolicy retryPolicy;

    public void submit(UUID replayJobId) {
        executorService.submit(() -> runJob(replayJobId));
    }

    void runJob(UUID jobId) {
        ReplayJob job = replayJobRepository.findByIdWithEvent(jobId).orElseThrow();

        if (!job.isQueued()) {
            log.warn("ReplayJob {} is not QUEUED, skipping execution", jobId);
            return;
        }

        job.markRunning();
        replayJobRepository.save(job);

        for (int attemptNo = 1; attemptNo <= retryPolicy.maxAttempts(); attemptNo++) {

            DeliveryAttempt attempt = executeAttempt(job, attemptNo);
            attemptRepository.save(attempt);

            if (attempt.isSuccess()) {
                job.markSuccess();
                replayJobRepository.save(job);
                return;
            }

            if (!retryPolicy.shouldRetry(attempt)) {
                break;
            }

            sleep(retryPolicy.backoff(attemptNo));
        }

        job.markFailed();
        replayJobRepository.save(job);
    }

    private DeliveryAttempt executeAttempt(ReplayJob job, int attemptNo) {
        DeliveryAttempt attempt = DeliveryAttempt.start(job, attemptNo);

        Instant start = Instant.now();

        try {
            HttpResult result = httpDeliveryClient.deliver(job);

            attempt.markResult(
                    result.status(),
                    result.responseBody(),
                    Duration.between(start, Instant.now())
            );

        } catch (Exception ex) {
            attempt.markFailure(ex, Duration.between(start, Instant.now()));
        }

        return attempt;
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

