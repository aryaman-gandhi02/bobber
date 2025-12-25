package com.bobber.event.mapper;

import com.bobber.event.domain.Event;
import com.bobber.event.dto.EventDetailDTO;
import com.bobber.event.policy.ContentTypePolicy;
import com.bobber.event.value.BodyPreview;
import com.bobber.security.policy.UnsafeHeadersPolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public final class EventDetailMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EventDetailMapper() {}

    public static EventDetailDTO from(Event event) {
        byte[] body = event.getBody();

        BodyPreview preview = buildBodyPreview(body, event.getContentType());

        return new EventDetailDTO(
                event.getId(),
                event.getMethod(),
                event.getPath(),
                event.getQueryParams(),
                sanitizeHeaders(event.getHeaders()),
                event.getContentType(),
                body != null ? body.length : 0,
                preview.preview(),
                preview.binary(),
                event.getReceivedAt()
        );
    }

    private static Map<String, List<String>> sanitizeHeaders(Map<String, List<String>> headers) {
        if (headers == null) {
            return Map.of();
        }

        return headers.entrySet().stream()
                .filter(e -> !UnsafeHeadersPolicy.isAlwaysBlocked(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private static BodyPreview buildBodyPreview(byte[] body, String contentType) {
        if (body == null || body.length == 0) {
            return BodyPreview.empty();
        }

        try {
            switch (ContentTypePolicy.determineContentType(contentType)) {
                case ContentTypePolicy.CONTENT_TYPE_JSON -> {
                    return BodyPreview.text(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(OBJECT_MAPPER.readTree(body)));
                }

                case ContentTypePolicy.CONTENT_TYPE_TEXTUAL -> {
                    return BodyPreview.text(new String(body, StandardCharsets.UTF_8));
                }

                default -> {
                    return BodyPreview.binary(Base64.getEncoder().encodeToString(body));
                }
            }
        } catch (Exception ignored) {}

        return BodyPreview.empty();
    }
}

