package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.security.RefreshTokenCookie;
import br.com.pacto.recrutamento.web.support.HttpResponses;
import br.com.pacto.recrutamento.web.request.auth.*;

import br.com.pacto.recrutamento.app.dtos.usuario.*;
import br.com.pacto.recrutamento.app.ports.in.usuario.UsuarioUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioUseCase service;
    private final RefreshTokenCookie refreshCookie;

    public AuthController(UsuarioUseCase service, RefreshTokenCookie refreshCookie) {
        this.service = service;
        this.refreshCookie = refreshCookie;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<TypedResponse<UsuarioDTO>> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return HttpResponses.from(service.cadastrarUsuario(
                new CadastrarUsuarioDTO(request.nome, request.email, request.telefone, request.senha)));
    }

    @PostMapping("/login")
    public ResponseEntity<TypedResponse<SessaoDTO>> login(@Valid @RequestBody LoginRequest request) {
        return sessionResponse(service.autenticarUsuario(
                new AutenticarUsuarioDTO(request.email, request.senha)));
    }

    @GetMapping("/csrf")
    public ResponseEntity<TypedResponse<String>> csrf(CsrfToken token) {
        return ResponseEntity.ok(new TypedResponse<String>(200, null, token.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TypedResponse<SessaoDTO>> refresh(
            @CookieValue(name = RefreshTokenCookie.NAME) String refreshToken) {
        return sessionResponse(service.renovarSessao(new RenovarSessaoDTO(refreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<TypedResponse<Void>> logout(Authentication authentication,
            @CookieValue(name = RefreshTokenCookie.NAME) String refreshToken) {
        TypedResponse<Void> response = service.encerrarSessao(
                new EncerrarSessaoDTO(AuthenticatedUser.id(authentication), refreshToken));
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusCode());
        if (!response.getIsError()) builder.header(HttpHeaders.SET_COOKIE, refreshCookie.clear());
        return builder.body(response);
    }

    @PostMapping("/recuperacao-senha/solicitacoes")
    public ResponseEntity<TypedResponse<Void>> solicitarRecuperacao(
            @Valid @RequestBody RecuperacaoRequest request) {
        return HttpResponses.from(service.solicitarRecuperacaoSenha(
                new SolicitarRecuperacaoSenhaDTO(request.email)));
    }

    @PostMapping("/recuperacao-senha/confirmacoes")
    public ResponseEntity<TypedResponse<Void>> redefinirSenha(
            @Valid @RequestBody RedefinicaoRequest request) {
        return HttpResponses.from(service.redefinirSenha(
                new RedefinirSenhaDTO(request.token, request.novaSenha)));
    }

    private ResponseEntity<TypedResponse<SessaoDTO>> sessionResponse(
            TypedResponse<SessaoDTO> response) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusCode());
        if (!response.getIsError() && response.getData() != null) {
            builder.header(HttpHeaders.SET_COOKIE,
                    refreshCookie.create(response.getData().getRefreshToken()));
        }
        return builder.body(response);
    }

}
