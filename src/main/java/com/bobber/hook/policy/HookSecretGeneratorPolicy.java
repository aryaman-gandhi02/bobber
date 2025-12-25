package com.bobber.hook.policy;

import java.security.SecureRandom;
import java.util.Base64;

public final class HookSecretGeneratorPolicy {

    private static final int SECRET_BYTES = 32;
    private static final String BOBBER_SK_PREFIX = "bobber_sk_";

    private static final SecureRandom RANDOM = new SecureRandom();

    private HookSecretGeneratorPolicy() {
    }

    public static String generate() {
        byte[] bytes = new byte[SECRET_BYTES];
        RANDOM.nextBytes(bytes);

        String encoded = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        return BOBBER_SK_PREFIX + encoded;
    }
}

