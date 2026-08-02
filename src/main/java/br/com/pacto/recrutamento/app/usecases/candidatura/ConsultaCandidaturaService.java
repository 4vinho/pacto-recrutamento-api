package br.com.pacto.recrutamento.app.usecases.candidatura;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.USUARIO_NAO_AUTORIZADO_CONSULTAR_CANDIDATURA;
import static br.com.pacto.recrutamento.core.common.ErrorMessages.VAGA_NAO_ENCONTRADA;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ListarCandidaturasDaVagaDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ListarMinhasCandidaturasDTO;
import br.com.pacto.recrutamento.app.dtos.candidatura.ResumoCandidaturasDTO;
import br.com.pacto.recrutamento.app.ports.out.candidatura.AutorizacaoResponsavelCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.PerguntaCandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.VagaCandidaturaPort;
import br.com.pacto.recrutamento.core.common.FiltroRespostaCandidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
class ConsultaCandidaturaService {
    private final CandidaturaPort candidaturas;
    private final VagaCandidaturaPort vagas;
    private final PerguntaCandidaturaPort perguntas;
    private final AutorizacaoResponsavelCandidaturaPort autorizacao;
    private final CandidaturaDtoMapper mapper;

    ConsultaCandidaturaService(CandidaturaPort candidaturas,
                               VagaCandidaturaPort vagas,
                               PerguntaCandidaturaPort perguntas,
                               AutorizacaoResponsavelCandidaturaPort autorizacao,
                               CandidaturaDtoMapper mapper) {
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.autorizacao = autorizacao;
        this.mapper = mapper;
    }

    TypedPagedResponse<CandidaturaDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        if (query == null || query.getUsuarioId() == null || query.getPage() < 0
                || query.getPageSize() <= 0 || query.getPageSize() > 100) {
            return paginaInvalida("Consulta de candidaturas invalida", 0, 20);
        }
        PaginaGenerico<Candidatura> pagina = candidaturas.listarPorUsuario(
                query.getUsuarioId(), query.getStatus(), query.getInicio(), query.getFim(),
                query.getPage(), query.getPageSize());
        List<CandidaturaDTO> dados = pagina.getItens().stream().map(mapper::paraDto)
                .collect(Collectors.toList());
        return new TypedPagedResponse<>(200, "Candidaturas encontradas", dados,
                query.getPage(), query.getPageSize(), pagina.getTotalItens());
    }

    TypedResponse<ResumoCandidaturasDTO> resumirMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        if (query == null || query.getUsuarioId() == null
                || (query.getInicio() != null && query.getFim() != null
                && query.getInicio().isAfter(query.getFim()))) {
            return new TypedResponse<>(400, "Periodo de candidaturas invalido", null);
        }
        return new TypedResponse<>(200, "Resumo de candidaturas encontrado",
                new ResumoCandidaturasDTO(candidaturas.contarPorStatusDoUsuario(
                        query.getUsuarioId(), query.getInicio(), query.getFim())));
    }

    TypedPagedResponse<CandidaturaDTO> listarCandidaturasDaVaga(
            ListarCandidaturasDaVagaDTO query) {
        if (consultaDaVagaInvalida(query)) {
            int page = query == null || query.getPage() < 0 ? 0 : query.getPage();
            int size = query == null || query.getPageSize() <= 0 ? 20 : query.getPageSize();
            return paginaInvalida("Consulta de candidaturas invalida", page, size);
        }
        br.com.pacto.recrutamento.core.entities.Vaga vaga = vagas.buscarPorId(
                query.getVagaId()).orElse(null);
        if (vaga == null) {
            return paginaInvalida(VAGA_NAO_ENCONTRADA, query.getPage(), query.getPageSize(), 404);
        }
        if (!autorizacao.podeGerenciar(query.getUsuarioId(), vaga)) {
            return paginaInvalida(USUARIO_NAO_AUTORIZADO_CONSULTAR_CANDIDATURA,
                    query.getPage(), query.getPageSize(), 403);
        }
        if (!filtrosRespostasValidos(query)) {
            return paginaInvalida("Filtros de respostas invalidos",
                    query.getPage(), query.getPageSize());
        }
        PaginaGenerico<Candidatura> pagina = candidaturas.listarPorVaga(query.getVagaId(),
                query.getStatus(), query.getBusca(), query.getFiltrosRespostas(),
                query.getRequisitoId(), query.getNivelMinimo(), query.getAtendeTodosRequisitos(),
                query.getPage(), query.getPageSize());
        List<CandidaturaDTO> dados = pagina.getItens().stream()
                .map(mapper::paraDtoComRequisitos).collect(Collectors.toList());
        return new TypedPagedResponse<>(200, "Candidaturas encontradas", dados,
                query.getPage(), query.getPageSize(), pagina.getTotalItens());
    }

    private boolean consultaDaVagaInvalida(ListarCandidaturasDaVagaDTO query) {
        return query == null || query.getUsuarioId() == null || query.getVagaId() == null
                || query.getPage() < 0 || query.getPageSize() <= 0 || query.getPageSize() > 100;
    }

    private boolean filtrosRespostasValidos(ListarCandidaturasDaVagaDTO query) {
        for (FiltroRespostaCandidatura filtro : query.getFiltrosRespostas()) {
            if (!filtroValido(query, filtro)) return false;
        }
        return true;
    }

    private boolean filtroValido(ListarCandidaturasDaVagaDTO query,
                                 FiltroRespostaCandidatura filtro) {
        if (filtro == null || filtro.getPerguntaId() == null || filtro.getOperador() == null
                || filtro.getValor() == null || filtro.getValor().trim().isEmpty()) return false;
        PerguntaVaga pergunta = perguntas.buscarAtivaPorId(filtro.getPerguntaId()).orElse(null);
        if (pergunta == null || !query.getVagaId().equals(pergunta.getVagaId())) return false;
        switch (pergunta.getTipoResposta()) {
            case NUMERO:
                return numeroValido(filtro) && operadorNumerico(filtro.getOperador());
            case DATA:
                return dataValida(filtro) && operadorData(filtro.getOperador());
            case BOOLEANO:
                return booleanoValido(filtro) && operadorIgualdade(filtro.getOperador());
            default:
                return !operadorOrdenacao(filtro.getOperador());
        }
    }

    private boolean numeroValido(FiltroRespostaCandidatura filtro) {
        try {
            Double.parseDouble(filtro.getValor());
            return true;
        } catch (NumberFormatException error) {
            return false;
        }
    }

    private boolean dataValida(FiltroRespostaCandidatura filtro) {
        try {
            LocalDate.parse(filtro.getValor());
            return true;
        } catch (DateTimeParseException error) {
            return false;
        }
    }

    private boolean booleanoValido(FiltroRespostaCandidatura filtro) {
        return "true".equalsIgnoreCase(filtro.getValor())
                || "false".equalsIgnoreCase(filtro.getValor());
    }

    private boolean operadorNumerico(FiltroRespostaCandidatura.Operador operador) {
        return operadorIgualdade(operador)
                || operador == FiltroRespostaCandidatura.Operador.MAIOR_QUE
                || operador == FiltroRespostaCandidatura.Operador.MAIOR_OU_IGUAL
                || operador == FiltroRespostaCandidatura.Operador.MENOR_QUE
                || operador == FiltroRespostaCandidatura.Operador.MENOR_OU_IGUAL;
    }

    private boolean operadorData(FiltroRespostaCandidatura.Operador operador) {
        return operadorIgualdade(operador)
                || operador == FiltroRespostaCandidatura.Operador.ANTES_DE
                || operador == FiltroRespostaCandidatura.Operador.DEPOIS_DE;
    }

    private boolean operadorIgualdade(FiltroRespostaCandidatura.Operador operador) {
        return operador == FiltroRespostaCandidatura.Operador.IGUAL
                || operador == FiltroRespostaCandidatura.Operador.DIFERENTE;
    }

    private boolean operadorOrdenacao(FiltroRespostaCandidatura.Operador operador) {
        return operador == FiltroRespostaCandidatura.Operador.MAIOR_QUE
                || operador == FiltroRespostaCandidatura.Operador.MAIOR_OU_IGUAL
                || operador == FiltroRespostaCandidatura.Operador.MENOR_QUE
                || operador == FiltroRespostaCandidatura.Operador.MENOR_OU_IGUAL
                || operador == FiltroRespostaCandidatura.Operador.ANTES_DE
                || operador == FiltroRespostaCandidatura.Operador.DEPOIS_DE;
    }

    private TypedPagedResponse<CandidaturaDTO> paginaInvalida(
            String mensagem, int page, int size) {
        return paginaInvalida(mensagem, page, size, 400);
    }

    private TypedPagedResponse<CandidaturaDTO> paginaInvalida(
            String mensagem, int page, int size, int status) {
        return new TypedPagedResponse<>(status, mensagem,
                Collections.<CandidaturaDTO>emptyList(), page, size, 0);
    }
}
