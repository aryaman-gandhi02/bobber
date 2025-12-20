package com.bobber.replay.executor;

import com.bobber.domain.DeliveryAttempt;
import com.bobber.domain.ReplayJob;
import com.bobber.domain.enums.ReplayJobStatus;
import com.bobber.replay.dto.HttpResult;
import com.bobber.replay.executor.client.HttpDeliveryClient;
import com.bobber.replay.policy.RetryPolicy;
import com.bobber.repository.DeliveryAttemptRepository;
import com.bobber.repository.ReplayJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public void submit(UUID replayJobId) {
        executorService.submit(() -> executeJob(replayJobId));
    }

    private void executeJob(UUID jobId) {
        // TODO replace w/ service call
        ReplayJob job = replayJobRepository.findById(jobId).orElseThrow();

        job.setStatus(ReplayJobStatus.RUNNING);
        replayJobRepository.save(job);

        for (int attemptNo = 1; attemptNo <= RetryPolicy.MAX_ATTEMPTS; attemptNo++) {

            DeliveryAttempt attempt = executeAttempt(job, attemptNo);
            attemptRepository.save(attempt);

            if (attempt.isSuccess()) {
                job.setStatus(ReplayJobStatus.COMPLETED);
                replayJobRepository.save(job);
                return;
            }

            sleep(RetryPolicy.backoff(attemptNo));
        }

        // retries exhausted
        job.setStatus(ReplayJobStatus.COMPLETED);
        replayJobRepository.save(job);
    }

    private DeliveryAttempt executeAttempt(ReplayJob job, int attemptNo) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.setReplayJob(job);
        attempt.setAttemptNumber(attemptNo);

        Instant start = Instant.now();

        try {
            HttpResult result = httpDeliveryClient.deliver(job);

            attempt.setHttpStatus(result.status());
            attempt.setSuccess(result.success());
            attempt.setResponseBody(result.responseBody());

        } catch (Exception ex) {
            attempt.setSuccess(false);
            attempt.setError(ex.getMessage());
        } finally {
            attempt.setDurationMs(Duration.between(start, Instant.now()).toMillis());
        }

        return attempt;
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}

