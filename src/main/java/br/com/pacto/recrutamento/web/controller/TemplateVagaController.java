package br.com.pacto.recrutamento.web.controller;

import br.com.pacto.recrutamento.web.config.OpenApiConfiguration;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
import br.com.pacto.recrutamento.web.support.HttpResponses;
import br.com.pacto.recrutamento.web.request.templatevaga.SalvarTemplateVagaRequest;
import br.com.pacto.recrutamento.web.request.vaga.SalvarPerguntaRequest;
import br.com.pacto.recrutamento.web.request.vaga.SalvarRequisitoRequest;

import br.com.pacto.recrutamento.app.dtos.templatevaga.*;
import br.com.pacto.recrutamento.app.dtos.vaga.VagaDTO;
import br.com.pacto.recrutamento.app.ports.in.templatevaga.TemplateVagaUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/templates-vaga")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class TemplateVagaController {
    private final TemplateVagaUseCase service;

    public TemplateVagaController(TemplateVagaUseCase service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<TypedPagedResponse<TemplateVagaDTO>> listar(
            Authentication auth, @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        TypedPagedResponse<TemplateVagaDTO> resposta = service.listarTemplates(
                new ListarTemplatesVagaDTO(AuthenticatedUser.id(auth), busca, page, pageSize));
        return ResponseEntity.status(resposta.getStatusCode()).body(resposta);
    }

    @GetMapping("/{templateId}")
    public ResponseEntity<TypedResponse<TemplateVagaDetalheDTO>> consultar(
            Authentication auth, @PathVariable UUID templateId) {
        return HttpResponses.from(service.consultarTemplate(new ConsultarTemplateVagaDTO(
                AuthenticatedUser.id(auth), templateId)));
    }

    @PostMapping
    public ResponseEntity<TypedResponse<TemplateVagaDTO>> criar(
            Authentication auth, @Valid @RequestBody SalvarTemplateVagaRequest request) {
        return HttpResponses.from(service.criarTemplate(new CriarTemplateVagaDTO(
                AuthenticatedUser.id(auth), request.titulo, request.descricao)));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<TypedResponse<TemplateVagaDTO>> atualizar(
            Authentication auth, @PathVariable UUID templateId,
            @Valid @RequestBody SalvarTemplateVagaRequest request) {
        return HttpResponses.from(service.atualizarTemplate(new AtualizarTemplateVagaDTO(
                AuthenticatedUser.id(auth), templateId, request.titulo, request.descricao)));
    }

    @DeleteMapping("/{templateId}")
    public ResponseEntity<TypedResponse<Void>> excluir(
            Authentication auth, @PathVariable UUID templateId) {
        return HttpResponses.from(service.excluirTemplate(new ExcluirItemTemplateVagaDTO(
                AuthenticatedUser.id(auth), templateId, null)));
    }

    @PostMapping("/{templateId}/perguntas")
    public ResponseEntity<TypedResponse<PerguntaTemplateVagaDTO>> criarPergunta(
            Authentication auth, @PathVariable UUID templateId,
            @Valid @RequestBody SalvarPerguntaRequest request) {
        return HttpResponses.from(service.criarPerguntaDoTemplate(pergunta(
                AuthenticatedUser.id(auth), templateId, null, request)));
    }

    @PutMapping("/{templateId}/perguntas/{perguntaId}")
    public ResponseEntity<TypedResponse<PerguntaTemplateVagaDTO>> atualizarPergunta(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID perguntaId,
            @Valid @RequestBody SalvarPerguntaRequest request) {
        return HttpResponses.from(service.atualizarPerguntaDoTemplate(pergunta(
                AuthenticatedUser.id(auth), templateId, perguntaId, request)));
    }

    @DeleteMapping("/{templateId}/perguntas/{perguntaId}")
    public ResponseEntity<TypedResponse<Void>> excluirPergunta(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID perguntaId) {
        return HttpResponses.from(service.excluirPerguntaDoTemplate(new ExcluirItemTemplateVagaDTO(
                AuthenticatedUser.id(auth), templateId, perguntaId)));
    }

    @PostMapping("/{templateId}/requisitos")
    public ResponseEntity<TypedResponse<RequisitoTemplateVagaDTO>> criarRequisito(
            Authentication auth, @PathVariable UUID templateId,
            @Valid @RequestBody SalvarRequisitoRequest request) {
        return HttpResponses.from(service.criarRequisitoDoTemplate(requisito(
                AuthenticatedUser.id(auth), templateId, null, request)));
    }

    @PutMapping("/{templateId}/requisitos/{requisitoId}")
    public ResponseEntity<TypedResponse<RequisitoTemplateVagaDTO>> atualizarRequisito(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID requisitoId,
            @Valid @RequestBody SalvarRequisitoRequest request) {
        return HttpResponses.from(service.atualizarRequisitoDoTemplate(requisito(
                AuthenticatedUser.id(auth), templateId, requisitoId, request)));
    }

    @DeleteMapping("/{templateId}/requisitos/{requisitoId}")
    public ResponseEntity<TypedResponse<Void>> excluirRequisito(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID requisitoId) {
        return HttpResponses.from(service.excluirRequisitoDoTemplate(new ExcluirItemTemplateVagaDTO(
                AuthenticatedUser.id(auth), templateId, requisitoId)));
    }

    @PostMapping("/{templateId}/vagas")
    public ResponseEntity<TypedResponse<VagaDTO>> criarVaga(
            Authentication auth, @PathVariable UUID templateId) {
        return HttpResponses.from(service.criarVagaAPartirDoTemplate(
                new CriarVagaAPartirDoTemplateDTO(AuthenticatedUser.id(auth), templateId)));
    }

    private SalvarPerguntaTemplateVagaDTO pergunta(
            UUID usuarioId, UUID templateId, UUID perguntaId, SalvarPerguntaRequest request) {
        return new SalvarPerguntaTemplateVagaDTO(usuarioId, templateId, perguntaId,
                request.enunciado, request.tipoResposta, request.obrigatoria, request.ordem);
    }

    private SalvarRequisitoTemplateVagaDTO requisito(
            UUID usuarioId, UUID templateId, UUID requisitoId, SalvarRequisitoRequest request) {
        return new SalvarRequisitoTemplateVagaDTO(
                usuarioId, templateId, requisitoId, request.descricao, request.obrigatorio);
    }

}
