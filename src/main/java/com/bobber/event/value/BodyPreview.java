package com.bobber.event.value;

public record BodyPreview(String preview, boolean binary) {
    public static BodyPreview empty() {
        return new BodyPreview("", false);
    }

    public static BodyPreview text(String text) {
        return new BodyPreview(text, false);
    }

    public static BodyPreview binary(String base64) {
        return new BodyPreview(base64, true);
    }
}
