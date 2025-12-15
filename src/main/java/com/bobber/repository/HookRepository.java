package com.bobber.repository;

import com.bobber.domain.Hook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HookRepository extends JpaRepository<Hook, UUID> {
}
