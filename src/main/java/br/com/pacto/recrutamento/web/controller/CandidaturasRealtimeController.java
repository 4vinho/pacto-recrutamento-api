package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ListarCandidaturasDaVagaDTO;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.realtime.QuadroCandidaturasSse;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidaturasRealtimeController {
    private final CandidaturaUseCase service;
    private final QuadroCandidaturasSse realtime;

    public CandidaturasRealtimeController(
            CandidaturaUseCase service, QuadroCandidaturasSse realtime) {
        this.service = service;
        this.realtime = realtime;
    }

    @GetMapping(value = "/vagas/{vagaId}/candidaturas/eventos", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> acompanhar(
            Authentication authentication, @PathVariable UUID vagaId) {
        TypedPagedResponse<CandidaturaDTO> acesso = service.listarCandidaturasDaVaga(
                new ListarCandidaturasDaVagaDTO(
                        AuthenticatedUser.id(authentication), vagaId, null, 0, 1));
        if (acesso.getStatusCode() != 200) {
            return ResponseEntity.status(acesso.getStatusCode()).build();
        }
        return ResponseEntity.ok(realtime.assinar(vagaId));
    }
}
