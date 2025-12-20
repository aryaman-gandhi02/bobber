package com.bobber.service;

import com.bobber.api.dto.ReplayRequestDTO;
import com.bobber.domain.Event;
import com.bobber.domain.ReplayJob;
import com.bobber.domain.enums.ReplayJobStatus;
import com.bobber.replay.executor.ReplayExecutor;
import com.bobber.repository.ReplayJobRepository;
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

