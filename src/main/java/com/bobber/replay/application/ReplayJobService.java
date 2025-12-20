package com.bobber.replay.application;

import com.bobber.event.service.EventService;
import com.bobber.replay.dto.ReplayRequestDTO;
import com.bobber.event.domain.Event;
import com.bobber.replay.domain.ReplayJob;
import com.bobber.replay.domain.ReplayJobStatus;
import com.bobber.replay.repository.ReplayJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReplayJobService {

    private final EventService eventService;
    private final ReplayJobRepository replayJobRepository;
    private final ReplayExecutor replayExecutor;

    public UUID createJob(
            UUID eventId,
            String hookSecret,
            ReplayRequestDTO request
    ) {
        Event event = eventService.requireEvent(eventId, hookSecret);

        ReplayJob job = new ReplayJob();
        job.setEvent(event);
        job.setTargetUrl(request.targetUrl());
        job.setForwardAuthorization(Boolean.TRUE.equals(request.forwardAuthorization()));
        job.setHeaderOverrides(request.headerOverrides());
        job.setQueryParamOverrides(request.queryParamOverrides());
        job.setStatus(ReplayJobStatus.QUEUED);

        if (request.bodyOverrideBase64() != null) {
            job.setBodyOverride(Base64.getDecoder().decode(request.bodyOverrideBase64()));
            job.setContentTypeOverride(request.contentTypeOverride());
        }

        ReplayJob saved = replayJobRepository.save(job);

        replayExecutor.submit(saved.getId());

        return saved.getId();
    }
}

