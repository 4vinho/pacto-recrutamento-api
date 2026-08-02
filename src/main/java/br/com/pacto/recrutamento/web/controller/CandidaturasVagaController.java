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
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import br.com.pacto.recrutamento.core.common.FiltroRespostaCandidatura;

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
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) List<UUID> perguntaId,
            @RequestParam(required = false) List<FiltroRespostaCandidatura.Operador> operador,
            @RequestParam(required = false) List<String> valor,
            @RequestParam(required = false) UUID requisitoId,
            @RequestParam(required = false) NivelAtendimentoRequisito nivelMinimo,
            @RequestParam(required = false) Boolean atendeTodosRequisitos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<FiltroRespostaCandidatura> filtrosRespostas = criarFiltros(perguntaId, operador, valor);
        TypedPagedResponse<CandidaturaDTO> resposta = service.listarCandidaturasDaVaga(
                new ListarCandidaturasDaVagaDTO(AuthenticatedUser.id(authentication), vagaId,
                        status, busca, filtrosRespostas, requisitoId, nivelMinimo,
                        atendeTodosRequisitos, page, pageSize));
        return ResponseEntity.status(resposta.getStatusCode()).body(resposta);
    }

    private List<FiltroRespostaCandidatura> criarFiltros(List<UUID> perguntas,
            List<FiltroRespostaCandidatura.Operador> operadores, List<String> valores) {
        if (perguntas == null && operadores == null && valores == null) return Collections.emptyList();
        if (perguntas == null || operadores == null || valores == null
                || perguntas.size() != operadores.size() || perguntas.size() != valores.size()) {
            throw new IllegalArgumentException("Filtros de respostas invalidos");
        }
        List<FiltroRespostaCandidatura> filtros = new ArrayList<>();
        for (int i = 0; i < perguntas.size(); i++)
            filtros.add(new FiltroRespostaCandidatura(perguntas.get(i), operadores.get(i), valores.get(i)));
        return filtros;
    }

    @PostMapping("/vagas/{vagaId}/candidaturas")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> criar(
            Authentication authentication, @PathVariable UUID vagaId) {
        return HttpResponses.from(service.criarCandidatura(
                new CriarCandidaturaDTO(AuthenticatedUser.id(authentication), vagaId)));
    }

    @PostMapping("/vagas/{vagaId}/candidaturas/envio")
    public ResponseEntity<TypedResponse<CandidaturaDTO>> enviar(
            Authentication authentication, @PathVariable UUID vagaId,
            @Valid @RequestBody br.com.pacto.recrutamento.web.request.candidatura.EnviarCandidaturaRequest request) {
        List<br.com.pacto.recrutamento.app.dtos.candidatura.RespostaCandidaturaDTO> respostas =
                new ArrayList<>();
        for (br.com.pacto.recrutamento.web.request.candidatura.RespostaRequest resposta
                : request.respostas) {
            respostas.add(new br.com.pacto.recrutamento.app.dtos.candidatura.RespostaCandidaturaDTO(
                    resposta.perguntaId, resposta.valor));
        }
        List<br.com.pacto.recrutamento.app.dtos.candidatura.RespostaRequisitoCandidaturaDTO>
                requisitos = new ArrayList<>();
        for (br.com.pacto.recrutamento.web.request.candidatura.RespostaRequisitoRequest requisito
                : request.requisitos) {
            requisitos.add(new br.com.pacto.recrutamento.app.dtos.candidatura.RespostaRequisitoCandidaturaDTO(
                    requisito.requisitoId, requisito.nivel));
        }
        return HttpResponses.from(service.enviarCandidatura(
                new br.com.pacto.recrutamento.app.dtos.candidatura.EnviarCandidaturaDTO(
                        AuthenticatedUser.id(authentication), vagaId, respostas, requisitos)));
    }
}
