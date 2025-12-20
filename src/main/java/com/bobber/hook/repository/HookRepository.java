package com.bobber.hook.repository;

import com.bobber.hook.domain.Hook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HookRepository extends JpaRepository<Hook, UUID> {
}
