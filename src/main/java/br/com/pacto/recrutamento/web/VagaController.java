package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.app.dtos.vaga.*;
import br.com.pacto.recrutamento.app.services.VagaService;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.enums.StatusVaga;
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
@RequestMapping("/vagas")
public class VagaController {
    private final VagaService service;

    public VagaController(VagaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TypedResponse<VagaDTO>> criar(
            Authentication auth, @Valid @RequestBody VagaRequest request) {
        return HttpResponses.from(service.criarVaga(
                new CriarVagaDTO(AuthenticatedUser.id(auth), request.titulo, request.descricao)));
    }

    @PutMapping("/{vagaId}")
    public ResponseEntity<TypedResponse<VagaDTO>> atualizar(
            Authentication auth, @PathVariable UUID vagaId, @Valid @RequestBody VagaRequest request) {
        return HttpResponses.from(service.atualizarVaga(new AtualizarVagaDTO(
                AuthenticatedUser.id(auth), vagaId, request.titulo, request.descricao)));
    }

    @PatchMapping("/{vagaId}/status")
    public ResponseEntity<TypedResponse<VagaDTO>> alterarStatus(
            Authentication auth, @PathVariable UUID vagaId, @Valid @RequestBody StatusRequest request) {
        return HttpResponses.from(service.alterarStatusVaga(
                new AlterarStatusVagaDTO(AuthenticatedUser.id(auth), vagaId, request.status)));
    }

    @DeleteMapping("/{vagaId}")
    public ResponseEntity<TypedResponse<Void>> excluir(Authentication auth, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.excluirVaga(
                new ExcluirVagaDTO(AuthenticatedUser.id(auth), vagaId)));
    }

    @PostMapping("/{vagaId}/perguntas")
    public ResponseEntity<TypedResponse<PerguntaVagaDTO>> criarPergunta(
            Authentication auth, @PathVariable UUID vagaId, @Valid @RequestBody PerguntaRequest request) {
        return HttpResponses.from(service.criarPerguntaDaVaga(pergunta(
                AuthenticatedUser.id(auth), vagaId, null, request)));
    }

    @PutMapping("/{vagaId}/perguntas/{perguntaId}")
    public ResponseEntity<TypedResponse<PerguntaVagaDTO>> atualizarPergunta(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID perguntaId,
            @Valid @RequestBody PerguntaRequest request) {
        return HttpResponses.from(service.atualizarPerguntaDaVaga(pergunta(
                AuthenticatedUser.id(auth), vagaId, perguntaId, request)));
    }

    @DeleteMapping("/{vagaId}/perguntas/{perguntaId}")
    public ResponseEntity<TypedResponse<Void>> excluirPergunta(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID perguntaId) {
        return HttpResponses.from(service.excluirPerguntaDaVaga(
                new ExcluirItemVagaDTO(AuthenticatedUser.id(auth), vagaId, perguntaId)));
    }

    @PostMapping("/{vagaId}/requisitos")
    public ResponseEntity<TypedResponse<RequisitoVagaDTO>> criarRequisito(
            Authentication auth, @PathVariable UUID vagaId, @Valid @RequestBody RequisitoRequest request) {
        return HttpResponses.from(service.criarRequisitoDaVaga(requisito(
                AuthenticatedUser.id(auth), vagaId, null, request)));
    }

    @PutMapping("/{vagaId}/requisitos/{requisitoId}")
    public ResponseEntity<TypedResponse<RequisitoVagaDTO>> atualizarRequisito(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID requisitoId,
            @Valid @RequestBody RequisitoRequest request) {
        return HttpResponses.from(service.atualizarRequisitoDaVaga(requisito(
                AuthenticatedUser.id(auth), vagaId, requisitoId, request)));
    }

    @DeleteMapping("/{vagaId}/requisitos/{requisitoId}")
    public ResponseEntity<TypedResponse<Void>> excluirRequisito(
            Authentication auth, @PathVariable UUID vagaId, @PathVariable UUID requisitoId) {
        return HttpResponses.from(service.excluirRequisitoDaVaga(
                new ExcluirItemVagaDTO(AuthenticatedUser.id(auth), vagaId, requisitoId)));
    }

    private SalvarPerguntaVagaDTO pergunta(UUID usuarioId, UUID vagaId, UUID perguntaId,
                                            PerguntaRequest request) {
        return new SalvarPerguntaVagaDTO(usuarioId, vagaId, perguntaId, request.enunciado,
                request.tipoResposta, request.obrigatoria, request.ordem);
    }

    private SalvarRequisitoVagaDTO requisito(UUID usuarioId, UUID vagaId, UUID requisitoId,
                                              RequisitoRequest request) {
        return new SalvarRequisitoVagaDTO(
                usuarioId, vagaId, requisitoId, request.descricao, request.obrigatorio);
    }

    public static class VagaRequest {
        @NotBlank public String titulo;
        @NotBlank public String descricao;
    }

    public static class StatusRequest {
        @NotNull public StatusVaga status;
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
