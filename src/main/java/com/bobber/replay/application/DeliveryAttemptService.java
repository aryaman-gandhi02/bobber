package com.bobber.replay.application;

import com.bobber.replay.dto.DeliveryAttemptDTO;
import com.bobber.replay.repository.DeliveryAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryAttemptService {

    private final DeliveryAttemptRepository deliveryAttemptRepository;

    public int getDeliveryAttemptCountForJobId(UUID replayJobId) {
        return deliveryAttemptRepository.countByReplayJobId(replayJobId);
    }

    public Page<DeliveryAttemptDTO> listAttempts(UUID replayJobId, Pageable pageable) {
        return deliveryAttemptRepository.findByReplayJobIdOrderByAttemptNumberAsc(replayJobId, pageable)
                .map(attempt -> new DeliveryAttemptDTO(
                        attempt.getAttemptNumber(),
                        attempt.getStatus(),
                        attempt.getHttpStatus(),
                        attempt.getDurationMs(),
                        attempt.getError(),
                        attempt.getAttemptedAt()
                ));
    }
}
