package com.bobber.repository;

import com.bobber.domain.ReplayJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReplayJobRepository extends JpaRepository<ReplayJob, UUID> {
}
