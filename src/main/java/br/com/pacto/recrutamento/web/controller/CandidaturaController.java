package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;
import br.com.pacto.recrutamento.web.request.candidatura.*;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@RestController
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidaturaController {
    private final CandidaturaUseCase service;

    public CandidaturaController(CandidaturaUseCase service) {
        this.service = service;
    }

    @GetMapping("/candidaturas/me")
    public ResponseEntity<TypedPagedResponse<CandidaturaDTO>> listarMinhas(
            Authentication authentication,
            @RequestParam(required = false) StatusCandidatura status,
            @RequestParam(required = false) OffsetDateTime inicio,
            @RequestParam(required = false) OffsetDateTime fim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        TypedPagedResponse<CandidaturaDTO> resposta = service.listarMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(
                        AuthenticatedUser.id(authentication), page, pageSize, status, inicio, fim));
        return ResponseEntity.status(resposta.getStatusCode()).body(resposta);
    }

    @GetMapping("/candidaturas/me/resumo")
    public ResponseEntity<TypedResponse<ResumoCandidaturasDTO>> resumirMinhas(
            Authentication authentication,
            @RequestParam(required = false) OffsetDateTime inicio,
            @RequestParam(required = false) OffsetDateTime fim) {
        return HttpResponses.from(service.resumirMinhasCandidaturas(
                new ListarMinhasCandidaturasDTO(
                        AuthenticatedUser.id(authentication), 0, 1, null, inicio, fim)));
    }

    @PostMapping("/candidaturas/{candidaturaId}/respostas")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> responder(
            Authentication authentication, @PathVariable UUID candidaturaId,
            @Valid @RequestBody RespostasRequest request) {
        List<RespostaCandidaturaDTO> respostas = new ArrayList<>();
        for (RespostaRequest resposta : request.respostas) {
            respostas.add(new RespostaCandidaturaDTO(resposta.perguntaId, resposta.valor));
        }
        return HttpResponses.from(service.registrarRespostas(new RegistrarRespostasDTO(
                AuthenticatedUser.id(authentication), candidaturaId, respostas)));
    }

    @PostMapping("/candidaturas/{candidaturaId}/requisitos")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> responderRequisitos(
            Authentication authentication, @PathVariable UUID candidaturaId,
            @Valid @RequestBody RequisitosRequest request) {
        List<RespostaRequisitoCandidaturaDTO> respostas = new ArrayList<>();
        for (RespostaRequisitoRequest resposta : request.respostas) {
            respostas.add(new RespostaRequisitoCandidaturaDTO(
                    resposta.requisitoId, resposta.nivel));
        }
        return HttpResponses.from(service.registrarRequisitos(new RegistrarRequisitosDTO(
                AuthenticatedUser.id(authentication), candidaturaId, respostas)));
    }

    @PatchMapping("/candidaturas/{candidaturaId}/status")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> atualizarStatus(
            Authentication authentication, @PathVariable UUID candidaturaId,
            @Valid @RequestBody AtualizarStatusCandidaturaRequest request) {
        return HttpResponses.from(service.atualizarStatusCandidatura(
                new AtualizarStatusCandidaturaDTO(
                        AuthenticatedUser.id(authentication), candidaturaId, request.status,
                        request.feedback, request.versao)));
    }

    @PostMapping("/candidaturas/{candidaturaId}/cancelamento")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> cancelar(
            Authentication authentication, @PathVariable UUID candidaturaId) {
        return HttpResponses.from(service.cancelarCandidatura(
                new CancelarCandidaturaDTO(AuthenticatedUser.id(authentication), candidaturaId)));
    }

    @GetMapping("/candidaturas/{candidaturaId}")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> consultar(
            Authentication authentication, @PathVariable UUID candidaturaId) {
        return HttpResponses.from(service.consultarCandidatura(
                new ConsultarCandidaturaDTO(AuthenticatedUser.id(authentication), candidaturaId)));
    }

}
