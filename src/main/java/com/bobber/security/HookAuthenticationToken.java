package com.bobber.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.List;

public class HookAuthenticationToken extends AbstractAuthenticationToken {

    private final String secret;

    public HookAuthenticationToken(String secret) {
        super(List.of());
        this.secret = secret;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return secret;
    }

    @Override
    public Object getCredentials() {
        return secret;
    }
}

