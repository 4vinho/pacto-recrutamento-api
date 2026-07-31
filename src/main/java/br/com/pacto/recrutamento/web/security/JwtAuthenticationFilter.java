package br.com.pacto.recrutamento.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey key;

    public JwtAuthenticationFilter(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authenticate(authorization.substring(7));
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, authorities(claims.get("roles")));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException exception) {
            SecurityContextHolder.clearContext();
        }
    }

    private Collection<SimpleGrantedAuthority> authorities(Object rolesClaim) {
        if (!(rolesClaim instanceof List<?>)) {
            return Collections.emptyList();
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Object role : (List<?>) rolesClaim) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + String.valueOf(role)));
        }
        return authorities;
    }
}
