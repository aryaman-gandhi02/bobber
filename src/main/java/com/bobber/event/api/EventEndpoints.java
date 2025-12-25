package com.bobber.event.api;

public final class EventEndpoints {

    public static final String INGEST_BASE = "/hook";
    public static final String INGEST_WILDCARD = INGEST_BASE + "/**";

    private EventEndpoints() {
    }
}
