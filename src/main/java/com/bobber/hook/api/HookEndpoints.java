package com.bobber.hook.api;

public final class HookEndpoints {

    private HookEndpoints() {}

    // Base paths
    public static final String INGEST_BASE = "/hook";
    public static final String API_BASE = "/api";

    // Wildcards (for security)
    public static final String INGEST_WILDCARD = INGEST_BASE + "/**";
    public static final String API_WILDCARD = API_BASE + "/**";
}
