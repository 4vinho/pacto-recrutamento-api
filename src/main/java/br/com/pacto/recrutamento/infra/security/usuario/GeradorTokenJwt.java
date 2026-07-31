package br.com.pacto.recrutamento.infra.security.usuario;

import br.com.pacto.recrutamento.app.ports.usuario.GeradorToken;
import br.com.pacto.recrutamento.core.entities.Papel;
import br.com.pacto.recrutamento.core.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class GeradorTokenJwt implements GeradorToken {
    private final SecretKey chave;
    private final SecureRandom aleatorio = new SecureRandom();

    public GeradorTokenJwt(String segredo) {
        if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("security.jwt.secret deve possuir ao menos 256 bits.");
        }
        chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarAccessToken(Usuario usuario, OffsetDateTime expiraEm) {
        return Jwts.builder().id(UUID.randomUUID().toString()).subject(usuario.getId().toString())
                .claim("roles", usuario.getPapeis().stream().map(Papel::getNome).map(Enum::name).collect(Collectors.toList()))
                .issuedAt(new Date()).expiration(Date.from(expiraEm.toInstant())).signWith(chave).compact();
    }

    public String gerarTokenAleatorio() {
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String calcularHash(String token) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 indisponível.", exception);
        }
        StringBuilder hexadecimal = new StringBuilder(64);
        for (byte b : hash) hexadecimal.append(String.format("%02x", b));
        return hexadecimal.toString();
    }
}
