package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;
import br.com.pacto.recrutamento.web.request.vaga.*;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.ports.in.vaga.VagaUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/vagas")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class VagaController {
    private final VagaUseCase service;

    public VagaController(VagaUseCase service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA', 'CANDIDATO')")
    public ResponseEntity<TypedPagedResponse<VagaDTO>> listar(
            Authentication auth,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) StatusVaga status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "criadoEm") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direcao) {
        TypedPagedResponse<VagaDTO> resposta = service.listarVagas(new ListarVagasDTO(
                AuthenticatedUser.id(auth), busca, status, page, pageSize, ordenarPor,
                "asc".equalsIgnoreCase(direcao)));
        return ResponseEntity.status(resposta.getStatusCode()).body(resposta);
    }

    @GetMapping("/{vagaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA', 'CANDIDATO')")
    public ResponseEntity<TypedResponse<VagaDetalheDTO>> consultar(
            Authentication auth, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.consultarVaga(
                new ConsultarVagaDTO(AuthenticatedUser.id(auth), vagaId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<VagaDTO>> criar(
            Authentication auth, @Valid @RequestBody SalvarVagaRequest request) {
        return HttpResponses.from(service.criarVaga(
                new CriarVagaDTO(AuthenticatedUser.id(auth), request.responsaveisIds,
                        request.titulo, request.descricao)));
    }

    @PostMapping("/completa")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<VagaDetalheDTO>> criarCompleta(
            Authentication auth, @Valid @RequestBody CriarVagaCompletaRequest request) {
        UUID usuarioId = AuthenticatedUser.id(auth);
        Set<UUID> responsaveis = request.responsaveisIds == null || request.responsaveisIds.isEmpty()
                ? Collections.singleton(usuarioId) : request.responsaveisIds;
        List<CriarVagaCompletaDTO.Pergunta> perguntas = request.perguntas.stream()
                .map(item -> new CriarVagaCompletaDTO.Pergunta(item.enunciado, item.tipoResposta,
                        item.obrigatoria, item.ordem)).collect(java.util.stream.Collectors.toList());
        List<CriarVagaCompletaDTO.Requisito> requisitos = request.requisitos.stream()
                .map(item -> new CriarVagaCompletaDTO.Requisito(item.descricao, item.obrigatorio))
                .collect(java.util.stream.Collectors.toList());
        return HttpResponses.from(service.criarVagaCompleta(new CriarVagaCompletaDTO(usuarioId,
                responsaveis, request.titulo, request.descricao, perguntas, requisitos)));
    }

    @PutMapping("/{vagaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<VagaDTO>> atualizar(
            Authentication auth, @PathVariable UUID vagaId,
            @Valid @RequestBody SalvarVagaRequest request) {
        return HttpResponses.from(service.atualizarVaga(new AtualizarVagaDTO(
                AuthenticatedUser.id(auth), vagaId, request.responsaveisIds,
                request.titulo, request.descricao)));
    }

    @PatchMapping("/{vagaId}/status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<VagaDTO>> alterarStatus(
            Authentication auth, @PathVariable UUID vagaId,
            @Valid @RequestBody AlterarStatusVagaRequest request) {
        return HttpResponses.from(service.alterarStatusVaga(
                new AlterarStatusVagaDTO(AuthenticatedUser.id(auth), vagaId, request.status)));
    }

    @DeleteMapping("/{vagaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<Void>> excluir(Authentication auth, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.excluirVaga(
                new ExcluirVagaDTO(AuthenticatedUser.id(auth), vagaId)));
    }

    @PostMapping("/{vagaId}/perguntas")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<PerguntaVagaDTO>> criarPergunta(
            Authentication auth, @PathVariable UUID vagaId,
            @Valid @RequestBody SalvarPerguntaRequest request) {
        return HttpResponses.from(service.criarPerguntaDaVaga(pergunta(
                AuthenticatedUser.id(auth), vagaId, null, request)));
    }

    @PutMapping("/{vagaId}/perguntas/{perguntaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<PerguntaVagaDTO>> atualizarPergunta(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID perguntaId,
            @Valid @RequestBody SalvarPerguntaRequest request) {
        return HttpResponses.from(service.atualizarPerguntaDaVaga(pergunta(
                AuthenticatedUser.id(auth), vagaId, perguntaId, request)));
    }

    @DeleteMapping("/{vagaId}/perguntas/{perguntaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<Void>> excluirPergunta(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID perguntaId) {
        return HttpResponses.from(service.excluirPerguntaDaVaga(
                new ExcluirItemVagaDTO(AuthenticatedUser.id(auth), vagaId, perguntaId)));
    }

    @PostMapping("/{vagaId}/requisitos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<RequisitoVagaDTO>> criarRequisito(
            Authentication auth, @PathVariable UUID vagaId,
            @Valid @RequestBody SalvarRequisitoRequest request) {
        return HttpResponses.from(service.criarRequisitoDaVaga(requisito(
                AuthenticatedUser.id(auth), vagaId, null, request)));
    }

    @PutMapping("/{vagaId}/requisitos/{requisitoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<RequisitoVagaDTO>> atualizarRequisito(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID requisitoId,
            @Valid @RequestBody SalvarRequisitoRequest request) {
        return HttpResponses.from(service.atualizarRequisitoDaVaga(requisito(
                AuthenticatedUser.id(auth), vagaId, requisitoId, request)));
    }

    @DeleteMapping("/{vagaId}/requisitos/{requisitoId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RESPONSAVEL_VAGA')")
    public ResponseEntity<TypedResponse<Void>> excluirRequisito(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID requisitoId) {
        return HttpResponses.from(service.excluirRequisitoDaVaga(
                new ExcluirItemVagaDTO(AuthenticatedUser.id(auth), vagaId, requisitoId)));
    }

    private SalvarPerguntaVagaDTO pergunta(UUID usuarioId, UUID vagaId, UUID perguntaId,
                                           SalvarPerguntaRequest request) {
        return new SalvarPerguntaVagaDTO(usuarioId, vagaId, perguntaId, request.enunciado,
                request.tipoResposta, request.obrigatoria, request.ordem);
    }

    private SalvarRequisitoVagaDTO requisito(UUID usuarioId, UUID vagaId, UUID requisitoId,
                                             SalvarRequisitoRequest request) {
        return new SalvarRequisitoVagaDTO(
                usuarioId, vagaId, requisitoId, request.descricao, request.obrigatorio);
    }

}
