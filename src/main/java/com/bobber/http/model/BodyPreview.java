package com.bobber.http.model;

import com.bobber.event.policy.ContentTypePolicy;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record BodyPreview(String preview, boolean binary) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static BodyPreview empty() {
        return new BodyPreview("", false);
    }

    public static BodyPreview text(String text) {
        return new BodyPreview(text, false);
    }

    public static BodyPreview binary(String base64) {
        return new BodyPreview(base64, true);
    }

    public static BodyPreview buildBodyPreview(byte[] body, String contentType) {
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
        } catch (Exception ignored) {
        }

        return BodyPreview.empty();
    }
}
