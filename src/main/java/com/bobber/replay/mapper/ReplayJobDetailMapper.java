package com.bobber.replay.mapper;

import com.bobber.http.preview.BodyPreview;
import com.bobber.http.util.HeaderUtil;
import com.bobber.replay.domain.ReplayJob;
import com.bobber.replay.dto.ReplayJobDetailDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ReplayJobDetailMapper {
    private ReplayJobDetailMapper() {
    }

    public static ReplayJobDetailDTO from(ReplayJob replayJob) {
        byte[] bodyOverride = replayJob.getBodyOverride();

        BodyPreview bodyOverridePreview = BodyPreview.buildBodyPreview(bodyOverride, replayJob.getContentTypeOverride());

        return new ReplayJobDetailDTO(
                replayJob.getId(),
                replayJob.getStatus(),
                replayJob.getCreatedAt(),
                replayJob.getTargetUrl(),
                HeaderUtil.sanitizeHeaders(replayJob.getHeaderOverrides()),
                replayJob.getQueryParamOverrides(),
                bodyOverride != null ? bodyOverride.length : 0,
                bodyOverridePreview.preview(),
                bodyOverridePreview.binary(),
                replayJob.getContentTypeOverride(),
                replayJob.isForwardAuthorization()
        );
    }
}
