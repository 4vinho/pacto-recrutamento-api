package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.app.services.CandidaturaService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidaturaController {
    private final CandidaturaService service;

    public CandidaturaController(CandidaturaService service) {
        this.service = service;
    }

    @PostMapping("/vagas/{vagaId}/candidaturas")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> criar(
            Authentication authentication, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.criarCandidatura(
                new CriarCandidaturaDTO(AuthenticatedUser.id(authentication), vagaId)));
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

    @PatchMapping("/candidaturas/{candidaturaId}/status")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> atualizarStatus(
            Authentication authentication, @PathVariable UUID candidaturaId,
            @Valid @RequestBody StatusRequest request) {
        return HttpResponses.from(service.atualizarStatusCandidatura(
                new AtualizarStatusCandidaturaDTO(
                        AuthenticatedUser.id(authentication), candidaturaId, request.status)));
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

    public static class RespostasRequest {
        @NotEmpty @Valid public List<RespostaRequest> respostas;
    }

    public static class RespostaRequest {
        @NotNull public UUID perguntaId;
        @NotBlank public String valor;
    }

    public static class StatusRequest {
        @NotNull public StatusCandidatura status;
    }
}
