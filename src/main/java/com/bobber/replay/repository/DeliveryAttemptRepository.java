package com.bobber.replay.repository;

import com.bobber.replay.domain.DeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, UUID> {

    int countByReplayJobId(UUID replayJobId);

}
