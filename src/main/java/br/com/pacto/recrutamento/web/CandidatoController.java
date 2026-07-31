package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.app.dtos.candidato.*;
import br.com.pacto.recrutamento.app.ports.in.candidato.CandidatoUseCase;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@RequestMapping("/candidatos")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class CandidatoController {
    private final CandidatoUseCase service;

    public CandidatoController(CandidatoUseCase service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TypedResponse<CandidatoDTO>> criar(
            Authentication authentication, @Valid @RequestBody CandidatoRequest request) {
        return HttpResponses.from(service.criarCandidato(
                new CriarCandidatoDTO(AuthenticatedUser.id(authentication), request.dataAdmissao)));
    }

    @PutMapping("/me")
    public ResponseEntity<TypedResponse<CandidatoDTO>> atualizar(
            Authentication authentication, @Valid @RequestBody CandidatoRequest request) {
        return HttpResponses.from(service.atualizarCandidato(
                new AtualizarCandidatoDTO(AuthenticatedUser.id(authentication), request.dataAdmissao)));
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
        @NotNull public LocalDate dataAdmissao;
    }
}
