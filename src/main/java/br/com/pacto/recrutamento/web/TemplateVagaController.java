package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.app.dtos.templatevaga.*;
import br.com.pacto.recrutamento.app.dtos.vaga.VagaDTO;
import br.com.pacto.recrutamento.app.ports.in.templatevaga.TemplateVagaUseCase;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.enums.TipoResposta;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/templates-vaga")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTH)
public class TemplateVagaController {
    private final TemplateVagaUseCase service;

    public TemplateVagaController(TemplateVagaUseCase service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TypedResponse<TemplateVagaDTO>> criar(
            Authentication auth, @Valid @RequestBody TemplateRequest request) {
        return HttpResponses.from(service.criarTemplate(new CriarTemplateVagaDTO(
                AuthenticatedUser.id(auth), request.titulo, request.descricao)));
    }

    @PutMapping("/{templateId}")
    public ResponseEntity<TypedResponse<TemplateVagaDTO>> atualizar(
            Authentication auth, @PathVariable UUID templateId,
            @Valid @RequestBody TemplateRequest request) {
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
            @Valid @RequestBody PerguntaRequest request) {
        return HttpResponses.from(service.criarPerguntaDoTemplate(pergunta(
                AuthenticatedUser.id(auth), templateId, null, request)));
    }

    @PutMapping("/{templateId}/perguntas/{perguntaId}")
    public ResponseEntity<TypedResponse<PerguntaTemplateVagaDTO>> atualizarPergunta(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID perguntaId,
            @Valid @RequestBody PerguntaRequest request) {
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
            @Valid @RequestBody RequisitoRequest request) {
        return HttpResponses.from(service.criarRequisitoDoTemplate(requisito(
                AuthenticatedUser.id(auth), templateId, null, request)));
    }

    @PutMapping("/{templateId}/requisitos/{requisitoId}")
    public ResponseEntity<TypedResponse<RequisitoTemplateVagaDTO>> atualizarRequisito(
            Authentication auth, @PathVariable UUID templateId, @PathVariable UUID requisitoId,
            @Valid @RequestBody RequisitoRequest request) {
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
            UUID usuarioId, UUID templateId, UUID perguntaId, PerguntaRequest request) {
        return new SalvarPerguntaTemplateVagaDTO(usuarioId, templateId, perguntaId,
                request.enunciado, request.tipoResposta, request.obrigatoria, request.ordem);
    }

    private SalvarRequisitoTemplateVagaDTO requisito(
            UUID usuarioId, UUID templateId, UUID requisitoId, RequisitoRequest request) {
        return new SalvarRequisitoTemplateVagaDTO(
                usuarioId, templateId, requisitoId, request.descricao, request.obrigatorio);
    }

    public static class TemplateRequest {
        @NotBlank public String titulo;
        @NotBlank public String descricao;
    }

    public static class PerguntaRequest {
        @NotBlank public String enunciado;
        @NotNull public TipoResposta tipoResposta;
        public boolean obrigatoria;
        @Min(0) public int ordem;
    }

    public static class RequisitoRequest {
        @NotBlank public String descricao;
        public boolean obrigatorio;
    }
}
