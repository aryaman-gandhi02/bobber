package com.bobber.event.mapper;

import com.bobber.event.domain.Event;
import com.bobber.event.dto.EventDetailDTO;
import com.bobber.http.preview.BodyPreview;
import com.bobber.http.util.HeaderUtil;
import com.bobber.security.policy.UnsafeHeadersPolicy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class EventDetailMapper {

    private EventDetailMapper() {
    }

    public static EventDetailDTO from(Event event) {
        byte[] body = event.getBody();

        BodyPreview preview = BodyPreview.buildBodyPreview(body, event.getContentType());

        return new EventDetailDTO(
                event.getId(),
                event.getMethod(),
                event.getPath(),
                event.getQueryParams(),
                HeaderUtil.sanitizeHeaders(event.getHeaders()),
                event.getContentType(),
                body != null ? body.length : 0,
                preview.preview(),
                preview.binary(),
                event.getReceivedAt()
        );
    }
}

