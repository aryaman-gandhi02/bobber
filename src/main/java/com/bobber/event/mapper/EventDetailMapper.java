package com.bobber.event.mapper;

import com.bobber.event.domain.Event;
import com.bobber.event.dto.EventDetailDTO;
import com.bobber.http.model.BodyPreview;
import com.bobber.http.util.HeaderUtil;

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

