package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.CriarCandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ListarCandidaturasDaVagaDTO;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidaturasVagaController {
    private final CandidaturaUseCase service;

    public CandidaturasVagaController(CandidaturaUseCase service) {
        this.service = service;
    }

    @GetMapping("/vagas/{vagaId}/candidaturas")
    public ResponseEntity<TypedPagedResponse<CandidaturaDTO>> listar(
            Authentication authentication, @PathVariable UUID vagaId,
            @RequestParam(required = false) StatusCandidatura status,
            @RequestParam(required = false) NivelAtendimentoRequisito nivelMinimo,
            @RequestParam(required = false) Integer tempoEmpresaMeses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        TypedPagedResponse<CandidaturaDTO> resposta = service.listarCandidaturasDaVaga(
                new ListarCandidaturasDaVagaDTO(AuthenticatedUser.id(authentication), vagaId,
                        status, nivelMinimo, tempoEmpresaMeses, page, pageSize));
        return ResponseEntity.status(resposta.getStatusCode()).body(resposta);
    }

    @PostMapping("/vagas/{vagaId}/candidaturas")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> criar(
            Authentication authentication, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.criarCandidatura(
                new CriarCandidaturaDTO(AuthenticatedUser.id(authentication), vagaId)));
    }
}
