package br.com.pacto.recrutamento.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookie {
    public static final String NAME = "refresh_token";
    private static final Duration LIFETIME = Duration.ofDays(30);

    private final boolean secure;

    public RefreshTokenCookie(@Value("${security.cookie.secure:true}") boolean secure) {
        this.secure = secure;
    }

    public String create(String token) {
        return cookie(token, LIFETIME).toString();
    }

    public String clear() {
        return cookie("", Duration.ZERO).toString();
    }

    private ResponseCookie cookie(String value, Duration maxAge) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(maxAge)
                .build();
    }
}
