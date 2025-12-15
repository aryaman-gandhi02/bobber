package com.bobber.domain;

import com.bobber.domain.enums.HttpVerb;
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
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hook_id")
    private Hook hook;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpVerb method;

    @Column(nullable = false)
    private String path;

    @Column
    private String contentType;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String[]> queryParams;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> headers;

    @Column(columnDefinition = "bytea")
    private byte[] body;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant receivedAt;
}
