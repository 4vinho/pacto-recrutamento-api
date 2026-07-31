package br.com.pacto.recrutamento.web;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationFilter jwtFilter, ObjectMapper objectMapper)
            throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/auth/cadastro",
                        "/auth/login",
                        "/auth/refresh",
                        "/auth/recuperacao-senha/solicitacoes",
                        "/auth/recuperacao-senha/confirmacoes",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/actuator/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(),
                    new TypedResponse<Void>(401, NAO_AUTENTICADO, null,
                            ErrorCode.AUTHENTICATION_REQUIRED));
                })
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(),
                    new TypedResponse<Void>(403, ACESSO_NAO_AUTORIZADO_FORMATADO, null,
                            ErrorCode.ACCESS_DENIED));
                });
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
