package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;

import br.com.pacto.recrutamento.app.dtos.usuario.*;
import br.com.pacto.recrutamento.app.ports.in.usuario.UsuarioUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioUseCase service;

    public AuthController(UsuarioUseCase service) {
        this.service = service;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<TypedResponse<UsuarioDTO>> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return HttpResponses.from(service.cadastrarUsuario(
                new CadastrarUsuarioDTO(request.email, request.telefone, request.senha)));
    }

    @PostMapping("/login")
    public ResponseEntity<TypedResponse<SessaoDTO>> login(@Valid @RequestBody LoginRequest request) {
        return HttpResponses.from(service.autenticarUsuario(
                new AutenticarUsuarioDTO(request.email, request.senha)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TypedResponse<SessaoDTO>> refresh(@Valid @RequestBody TokenRequest request) {
        return HttpResponses.from(service.renovarSessao(new RenovarSessaoDTO(request.refreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<TypedResponse<Void>> logout(Authentication authentication,
                                                      @Valid @RequestBody TokenRequest request) {
        return HttpResponses.from(service.encerrarSessao(
                new EncerrarSessaoDTO(AuthenticatedUser.id(authentication), request.refreshToken)));
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

    public static class CadastroRequest {
        @NotBlank
        @Email
        public String email;
        @NotBlank
        public String telefone;
        @NotBlank
        @Size(min = 8, max = 72)
        public String senha;
    }

    public static class LoginRequest {
        @NotBlank
        @Email
        public String email;
        @NotBlank
        public String senha;
    }

    public static class TokenRequest {
        @NotBlank
        public String refreshToken;
    }

    public static class RecuperacaoRequest {
        @NotBlank
        @Email
        public String email;
    }

    public static class RedefinicaoRequest {
        @NotBlank
        public String token;
        @NotBlank
        @Size(min = 8, max = 72)
        public String novaSenha;
    }
}
