package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;

import br.com.pacto.recrutamento.app.dtos.candidato.*;
import br.com.pacto.recrutamento.app.ports.in.candidato.CandidatoUseCase;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/candidatos")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidatoController {
    private final CandidatoUseCase service;

    public CandidatoController(CandidatoUseCase service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<TypedResponse<CandidatoDTO>> consultar(Authentication authentication) {
        return HttpResponses.from(service.consultarMeuPerfil(
                new ConsultarMeuPerfilDTO(AuthenticatedUser.id(authentication))));
    }

    @PostMapping
    public ResponseEntity<TypedResponse<CandidatoDTO>> criar(
            Authentication authentication, @Valid @RequestBody CandidatoRequest request) {
        return HttpResponses.from(service.criarCandidato(
                new CriarCandidatoDTO(AuthenticatedUser.id(authentication), request.tituloProfissional,
                        request.resumoProfissional, request.experiencia, request.formacao,
                        request.habilidades)));
    }

    @PutMapping("/me")
    public ResponseEntity<TypedResponse<CandidatoDTO>> atualizar(
            Authentication authentication, @Valid @RequestBody CandidatoRequest request) {
        return HttpResponses.from(service.atualizarCandidato(
                new AtualizarCandidatoDTO(AuthenticatedUser.id(authentication), request.tituloProfissional,
                        request.resumoProfissional, request.experiencia, request.formacao,
                        request.habilidades)));
    }

    @GetMapping("/me/candidaturas")
    public ResponseEntity<TypedResponse<java.util.List<CandidaturaResumoDTO>>> listarCandidaturas(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        TypedPagedResponse<CandidaturaResumoDTO> response = service.listarMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(AuthenticatedUser.id(authentication), page, pageSize));
        return HttpResponses.from(response);
    }

    public static class CandidatoRequest {
        @NotBlank @Size(max = 120)
        public String tituloProfissional;
        @NotBlank @Size(max = 1000)
        public String resumoProfissional;
        @NotBlank @Size(max = 2000)
        public String experiencia;
        @NotBlank @Size(max = 1000)
        public String formacao;
        @Size(max = 500)
        public String habilidades;
    }
}
