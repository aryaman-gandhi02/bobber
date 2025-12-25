package com.bobber.replay.application;

import com.bobber.replay.repository.DeliveryAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryAttemptService {

    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public int getDeliveryAttemptCountForJobId(UUID replayJobId) {
        return deliveryAttemptRepository.countByReplayJobId(replayJobId);
    }

}
