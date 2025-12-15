package com.bobber.entity;

import com.bobber.enums.HttpVerb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
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

    @Column(columnDefinition = "jsonb")
    private String queryParams;

    @Column(columnDefinition = "jsonb")
    private String headers;

    @Column(columnDefinition = "text")
    private String body;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant receivedAt;
}
