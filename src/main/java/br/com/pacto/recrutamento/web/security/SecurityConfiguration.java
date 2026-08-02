package br.com.pacto.recrutamento.web.security;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    private static final String ADMINISTRADOR = "ADMINISTRADOR";
    private static final String RESPONSAVEL_VAGA = "RESPONSAVEL_VAGA";
    private static final String CANDIDATO = "CANDIDATO";

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationFilter jwtFilter, ObjectMapper objectMapper)
            throws Exception {
        CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepository.setCookiePath("/");
        http.csrf()
                .csrfTokenRepository(csrfRepository)
                .requireCsrfProtectionMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/auth/refresh", "POST"),
                        new AntPathRequestMatcher("/auth/logout", "POST")))
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/auth/cadastro",
                        "/auth/login",
                        "/auth/csrf",
                        "/auth/refresh",
                        "/auth/recuperacao-senha/solicitacoes",
                        "/auth/recuperacao-senha/confirmacoes",
                        "/v3/api-docs/**",
                        "/swagger",
                        "/swagger/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/actuator/health").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                .antMatchers(HttpMethod.GET, "/templates-vaga", "/templates-vaga/**")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA)
                .antMatchers("/templates-vaga/**").hasRole(ADMINISTRADOR)
                .antMatchers(HttpMethod.GET, "/vagas/*/candidaturas")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA)
                .antMatchers(HttpMethod.POST, "/vagas/*/candidaturas")
                    .hasRole(CANDIDATO)
                .antMatchers(HttpMethod.POST, "/vagas/*/candidaturas/envio")
                    .hasRole(CANDIDATO)
                .antMatchers(HttpMethod.GET, "/vagas/**")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA, CANDIDATO)
                .antMatchers("/vagas/**").hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA)
                .antMatchers(HttpMethod.GET, "/candidaturas/me/**").hasRole(CANDIDATO)
                .antMatchers(HttpMethod.POST, "/candidaturas/*/respostas")
                    .hasRole(CANDIDATO)
                .antMatchers(HttpMethod.POST, "/candidaturas/*/requisitos")
                    .hasRole(CANDIDATO)
                .antMatchers(HttpMethod.POST, "/candidaturas/*/cancelamento")
                    .hasRole(CANDIDATO)
                .antMatchers(HttpMethod.PATCH, "/candidaturas/*/status")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA)
                .antMatchers(HttpMethod.GET, "/candidaturas/*")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA, CANDIDATO)
                .antMatchers(HttpMethod.GET, "/candidaturas/*/curriculo/url")
                    .hasAnyRole(ADMINISTRADOR, RESPONSAVEL_VAGA, CANDIDATO)
                .antMatchers("/candidaturas/*/curriculo/**").hasRole(CANDIDATO)
                .anyRequest().denyAll()
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
