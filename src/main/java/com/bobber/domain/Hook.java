package com.bobber.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.time.Instant;
import java.util.UUID;

import static com.bobber.constants.Constants.BOBBER_SK_PREFIX;

@Entity
@Table(name = "hook")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hook {

    private static final StringKeyGenerator SECRET_GENERATOR = KeyGenerators.string();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false, unique = true)
    private String secret;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant expiresAt;

    @PrePersist
    void initSecret() {
        if (secret == null) {
            secret = BOBBER_SK_PREFIX + SECRET_GENERATOR.generateKey();
        }
    }
}
