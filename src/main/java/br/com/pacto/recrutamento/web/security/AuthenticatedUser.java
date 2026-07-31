package br.com.pacto.recrutamento.web.security;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class AuthenticatedUser {
    private AuthenticatedUser() {
    }

    public static UUID id(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (RuntimeException exception) {
            throw new UnauthenticatedException();
        }
    }

    public static final class UnauthenticatedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
