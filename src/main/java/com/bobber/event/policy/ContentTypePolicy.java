package com.bobber.event.policy;

public final class ContentTypePolicy {
    public static final String CONTENT_TYPE_TEXTUAL = "textual";
    public static final String CONTENT_TYPE_JSON = "json";
    public static final String CONTENT_TYPE_UNKNOWN = "unknown";
    private static final String APPLICATION_FORWARD_SLASH = "application/";
    private static final String TEXT_FORWARD_SLASH = "text/";
    private static final String JSON = "json";
    private static final String XML = "xml";
    private static final String FORM_DATA = "x-www-form-urlencoded";

    public static boolean isNotNull(String contentType) {
        return contentType != null;
    }

    public static boolean isJson(String contentType) {
        return isNotNull(contentType) && contentType.contains(JSON);
    }

    public static boolean isTextual(String contentType) {
        return isNotNull(contentType) && (
                contentType.startsWith(TEXT_FORWARD_SLASH)
                        || contentType.contains(XML)
                        || contentType.contains(FORM_DATA)
        );
    }

    public static String determineContentType(String contentType) {
        if (isJson(contentType)) return CONTENT_TYPE_JSON;
        if (isTextual(contentType)) return CONTENT_TYPE_TEXTUAL;
        return CONTENT_TYPE_UNKNOWN;
    }
}