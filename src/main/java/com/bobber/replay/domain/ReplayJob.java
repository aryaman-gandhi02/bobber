package com.bobber.replay.domain;

import com.bobber.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "replay_job",
        indexes = {
                @Index(name = "idx_event_id", columnList = "event_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_status_created_at", columnList = "status, created_at")
        }
)
public class ReplayJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "forward_authorization")
    private boolean forwardAuthorization;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "header_overrides", columnDefinition = "jsonb")
    private Map<String, String> headerOverrides;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "query_param_overrides", columnDefinition = "jsonb")
    private Map<String, String> queryParamOverrides;

    @Column(name = "body_override", columnDefinition = "bytea")
    private byte[] bodyOverride;

    @Column(name = "content_type_override")
    private String contentTypeOverride;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReplayJobStatus status = ReplayJobStatus.QUEUED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
