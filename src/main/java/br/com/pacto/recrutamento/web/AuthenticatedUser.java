package br.com.pacto.recrutamento.web;

import org.springframework.security.core.Authentication;

import java.util.UUID;

final class AuthenticatedUser {
    private AuthenticatedUser() {
    }

    static UUID id(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException();
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (RuntimeException exception) {
            throw new UnauthenticatedException();
        }
    }

    static final class UnauthenticatedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
