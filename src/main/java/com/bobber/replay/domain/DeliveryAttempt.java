package com.bobber.replay.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "delivery_attempt",
        indexes = {
                @Index(name = "idx_replay_job_id", columnList = "replay_job_id"),
                @Index(name = "idx_replay_job_id_attempted_at_desc", columnList = "replay_job_id, attempted_at DESC")
        }
)
public class DeliveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "replay_job_id", nullable = false)
    private ReplayJob replayJob;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryAttemptStatus status;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "error")
    private String error;

    @Column(name = "response_body", columnDefinition = "bytea")
    private byte[] responseBody;

    @CreationTimestamp
    @Column(name = "attempted_at", nullable = false, updatable = false)
    private Instant attemptedAt;

    public static DeliveryAttempt start(ReplayJob job, int attemptNo) {
        DeliveryAttempt attempt = new DeliveryAttempt();
        attempt.replayJob = job;
        attempt.attemptNumber = attemptNo;
        attempt.attemptedAt = Instant.now();
        return attempt;
    }

    public void markResult(int status, byte[] body, Duration duration) {
        this.httpStatus = status;
        this.responseBody = body;
        this.status = DeliveryAttemptStatus.SUCCESS;
        this.durationMs = duration.toMillis();
    }

    public void markFailure(Exception ex, Duration duration) {
        this.status = DeliveryAttemptStatus.FAILED;
        this.error = ex.getMessage();
        this.durationMs = duration.toMillis();
    }

    public boolean isSuccess() {
        return this.status == DeliveryAttemptStatus.SUCCESS;
    }
}
