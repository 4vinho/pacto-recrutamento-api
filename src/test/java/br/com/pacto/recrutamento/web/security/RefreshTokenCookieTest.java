package br.com.pacto.recrutamento.web.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RefreshTokenCookieTest {
    @Test
    void deveDisponibilizarCookieParaRotasPublicadasSobPrefixoDaApi() {
        String cookie = new RefreshTokenCookie(false).create("refresh-token");

        assertTrue(cookie.contains("Path=/"));
        assertTrue(cookie.contains("HttpOnly"));
        assertTrue(cookie.contains("SameSite=Strict"));
    }
}
