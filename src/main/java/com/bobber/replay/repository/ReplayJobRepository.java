package com.bobber.replay.repository;

import com.bobber.replay.domain.ReplayJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReplayJobRepository extends JpaRepository<ReplayJob, UUID> {
}
