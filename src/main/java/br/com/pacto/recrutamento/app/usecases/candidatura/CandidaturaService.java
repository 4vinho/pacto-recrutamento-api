package br.com.pacto.recrutamento.app.usecases.candidatura;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

import br.com.pacto.recrutamento.app.dtos.candidatura.*;
import br.com.pacto.recrutamento.app.ports.in.candidatura.CandidaturaUseCase;
import br.com.pacto.recrutamento.app.ports.out.candidatura.*;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.core.common.TypedPagedResponse;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.entities.*;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class CandidaturaService implements CandidaturaUseCase {
    private final CandidaturaPort candidaturas;
    private final VagaCandidaturaPort vagas;
    private final PerguntaCandidaturaPort perguntas;
    private final RequisitoCandidaturaPort requisitos;
    private final AutorizacaoResponsavelCandidaturaPort autorizacao;
    private final QuadroCandidaturasCachePort quadroCache;
    private final CandidaturaDtoMapper mapper;
    private final PublicadorEventosCandidatura publicadorEventos;

    public CandidaturaService(CandidaturaPort candidaturas,
                              VagaCandidaturaPort vagas,
                              PerguntaCandidaturaPort perguntas,
                              RequisitoCandidaturaPort requisitos,
                              AutorizacaoResponsavelCandidaturaPort autorizacao,
                              QuadroCandidaturasCachePort quadroCache,
                              CandidaturaDtoMapper mapper,
                              PublicadorEventosCandidatura publicadorEventos) {
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.quadroCache = quadroCache;
        this.mapper = mapper;
        this.publicadorEventos = publicadorEventos;
    }

    @Override
    @Transactional(readOnly = true)
    public TypedPagedResponse<CandidaturaDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        if (query == null || query.getUsuarioId() == null || query.getPage() < 0
                || query.getPageSize() <= 0 || query.getPageSize() > 100) {
            return new TypedPagedResponse<>(400, "Consulta de candidaturas invalida",
                    Collections.<CandidaturaDTO>emptyList(), 0, 20, 0);
        }
        PaginaGenerico<Candidatura> pagina = candidaturas.listarPorUsuario(
                query.getUsuarioId(), query.getStatus(), query.getInicio(), query.getFim(),
                query.getPage(), query.getPageSize());
        List<CandidaturaDTO> dados = pagina.getItens().stream().map(mapper::paraDto)
                .collect(java.util.stream.Collectors.toList());
        return new TypedPagedResponse<>(200, "Candidaturas encontradas", dados,
                query.getPage(), query.getPageSize(), pagina.getTotalItens());
    }

    @Override
    @Transactional(readOnly = true)
    public TypedResponse<ResumoCandidaturasDTO> resumirMinhasCandidaturas(
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

    @Override
    @Transactional(readOnly = true)
    public TypedPagedResponse<CandidaturaDTO> listarCandidaturasDaVaga(
            ListarCandidaturasDaVagaDTO query) {
        if (query == null || query.getUsuarioId() == null || query.getVagaId() == null
                || query.getPage() < 0 || query.getPageSize() <= 0 || query.getPageSize() > 100) {
            int page = query == null || query.getPage() < 0 ? 0 : query.getPage();
            int size = query == null || query.getPageSize() <= 0 ? 20 : query.getPageSize();
            return new TypedPagedResponse<>(400, "Consulta de candidaturas invalida",
                    Collections.<CandidaturaDTO>emptyList(), page, size, 0);
        }
        Vaga vaga = vagas.buscarPorId(query.getVagaId()).orElse(null);
        if (vaga == null) return new TypedPagedResponse<>(404, VAGA_NAO_ENCONTRADA,
                Collections.<CandidaturaDTO>emptyList(), query.getPage(), query.getPageSize(), 0);
        if (!autorizacao.podeGerenciar(query.getUsuarioId(), vaga)) {
            return new TypedPagedResponse<>(403, USUARIO_NAO_AUTORIZADO_CONSULTAR_CANDIDATURA,
                    Collections.<CandidaturaDTO>emptyList(), query.getPage(), query.getPageSize(), 0);
        }
        boolean consultaCompleta = query.getPage() == 0 && query.getPageSize() == 100
                && query.getStatus() == null && query.getNivelMinimo() == null
                && query.getTempoEmpresaMeses() == null;
        if (consultaCompleta) {
            Optional<List<CandidaturaDTO>> cache = quadroCache.buscar(query.getVagaId());
            if (cache.isPresent()) {
                return new TypedPagedResponse<>(200, "Candidaturas encontradas", cache.get(),
                        0, 100, cache.get().size());
            }
        }
        PaginaGenerico<Candidatura> pagina = candidaturas.listarPorVaga(query.getVagaId(),
                query.getStatus(), query.getNivelMinimo(), query.getTempoEmpresaMeses(),
                query.getPage(), query.getPageSize());
        if (consultaCompleta) {
            publicadorEventos.quadroConsultado(query.getVagaId(), pagina.getItens());
        }
        List<CandidaturaDTO> dados = pagina.getItens().stream().map(mapper::paraDto)
                .collect(java.util.stream.Collectors.toList());
        if (consultaCompleta) quadroCache.salvar(query.getVagaId(), dados);
        return new TypedPagedResponse<>(200, "Candidaturas encontradas", dados,
                query.getPage(), query.getPageSize(), pagina.getTotalItens());
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> criarCandidatura(CriarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getVagaId() == null) {
            return erro(400, DADOS_CANDIDATURA_INVALIDOS);
        }
        Vaga vaga = vagas.buscarPorId(command.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, VAGA_NAO_ENCONTRADA);
        }
        if (!vaga.aceitaCandidatura()) {
            return erro(422, VAGA_NAO_ACEITA_CANDIDATURAS);
        }
        if (candidaturas.existePorUsuarioIdEVagaId(command.getUsuarioId(), vaga.getId())) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        Candidatura candidatura = new Candidatura(command.getUsuarioId(), vaga.getId());
        boolean possuiPerguntas = !perguntas.listarAtivasPorVagaId(vaga.getId()).isEmpty();
        boolean possuiRequisitos = !requisitos.listarAtivosPorVagaId(vaga.getId()).isEmpty();
        candidatura.configurarEtapas(possuiPerguntas, possuiRequisitos);
        try {
            candidaturas.salvar(candidatura);
        } catch (CandidaturaPort.CandidaturaDuplicadaException ex) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) {
            publicarCriacao(candidatura);
        }
        String mensagem = candidatura.getStatus() == StatusCandidatura.RASCUNHO
                ? "Candidatura criada como rascunho" : "Candidatura criada";
        return new TypedResponse<>(201, mensagem, mapper.paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> registrarRespostas(RegistrarRespostasDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, LOTE_RESPOSTAS_INVALIDO);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_USUARIO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        if (candidatura.isPerguntasRespondidas()) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
        }
        List<PerguntaVaga> perguntasDaVaga = perguntas.listarAtivasPorVagaId(candidatura.getVagaId());
        Optional<List<RespostaCandidatura>> lote = ValidadorRespostasCandidatura.validarPerguntas(
                candidatura, command.getRespostas(), perguntasDaVaga);
        if (!lote.isPresent()) {
            return erro(422, LOTE_RESPOSTAS_INCOMPATIVEL);
        }
        try {
            candidaturas.registrarRespostasPerguntasAtomicamente(candidatura, lote.get());
        } catch (CandidaturaPort.RespostasDuplicadasException ex) {
            return erro(409, PERGUNTAS_JA_RESPONDIDAS);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) publicarCriacao(candidatura);
        return new TypedResponse<>(200, "Respostas registradas", mapper.paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> registrarRequisitos(RegistrarRequisitosDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null
                || command.getRespostas() == null || command.getRespostas().isEmpty()) {
            return erro(400, LOTE_REQUISITOS_INVALIDO);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_USUARIO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        if (candidatura.isRequisitosRespondidos()) {
            return erro(409, REQUISITOS_JA_RESPONDIDOS);
        }
        List<RequisitoVaga> requisitosDaVaga = requisitos.listarAtivosPorVagaId(
                candidatura.getVagaId());
        Optional<List<RespostaRequisitoCandidatura>> lote = ValidadorRespostasCandidatura.validarRequisitos(
                candidatura, command.getRespostas(), requisitosDaVaga);
        if (!lote.isPresent()) return erro(422, LOTE_REQUISITOS_INCOMPATIVEL);
        try {
            candidaturas.registrarRespostasRequisitosAtomicamente(candidatura, lote.get());
        } catch (CandidaturaPort.RequisitosJaRespondidosException ex) {
            return erro(409, REQUISITOS_JA_RESPONDIDOS);
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) publicarCriacao(candidatura);
        return new TypedResponse<>(200, "Requisitos registrados", mapper.paraDto(candidatura));
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> atualizarStatusCandidatura(AtualizarStatusCandidaturaDTO command) {
        if (command == null || command.getUsuarioSolicitanteId() == null || command.getCandidaturaId() == null
                || command.getStatus() == null) {
            return erro(400, DADOS_ATUALIZACAO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga == null) {
            return erro(404, VAGA_NAO_ENCONTRADA);
        }
        if (!autorizacao.podeGerenciar(command.getUsuarioSolicitanteId(), vaga)) {
            return erro(403, USUARIO_NAO_AUTORIZADO_ALTERAR_CANDIDATURA);
        }
        StatusCandidatura anterior = candidatura.getStatus();
        if (command.getVersao() != null && command.getVersao() != candidatura.getVersao()) {
            return erro(409, "A candidatura foi atualizada por outro usuario. Recarregue os dados.");
        }
        try {
            candidatura.setStatus(command.getStatus());
        } catch (IllegalArgumentException ex) {
            return erro(400, STATUS_CANDIDATURA_INVALIDO);
        } catch (IllegalStateException ex) {
            return erro(422, TRANSICAO_CANDIDATURA_INVALIDA);
        }
        candidaturas.salvar(candidatura);
        candidaturas.salvarHistorico(new HistoricoCandidatura(candidatura.getId(),
                command.getUsuarioSolicitanteId(), anterior, candidatura.getStatus(),
                command.getFeedback()));
        publicarAlteracao(candidatura, anterior);
        CandidaturaDTO atualizado = mapper.paraDto(candidatura);
        quadroCache.salvar(candidatura.getVagaId(), atualizado);
        return new TypedResponse<>(200, "Status da candidatura atualizado", atualizado);
    }

    @Override
    @Transactional
    public TypedResponse<CandidaturaDTO> cancelarCandidatura(CancelarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getCandidaturaId() == null) {
            return erro(400, DADOS_CANCELAMENTO_INVALIDOS);
        }
        Candidatura candidatura = candidaturas.buscarPorIdParaAtualizacao(
                command.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (!pertenceAoUsuario(candidatura, command.getUsuarioId())) {
            return erro(403, CANDIDATURA_NAO_PERTENCE_AO_USUARIO);
        }
        if (candidatura.getStatus() != StatusCandidatura.RASCUNHO
                && candidatura.getStatus() != StatusCandidatura.ENVIADA
                && candidatura.getStatus() != StatusCandidatura.EM_ANALISE) {
            return erro(422, CANCELAMENTO_NAO_PERMITIDO);
        }
        StatusCandidatura anterior = candidatura.getStatus();
        try {
            candidatura.cancelar(OffsetDateTime.now());
        } catch (IllegalArgumentException ex) {
            return erro(400, DADOS_CANCELAMENTO_INVALIDOS);
        } catch (IllegalStateException ex) {
            return erro(422, CANCELAMENTO_NAO_PERMITIDO);
        }
        candidaturas.salvar(candidatura);
        candidaturas.salvarHistorico(new HistoricoCandidatura(candidatura.getId(),
                command.getUsuarioId(), anterior, candidatura.getStatus(), null));
        publicarAlteracao(candidatura, anterior);
        return new TypedResponse<>(200, "Candidatura cancelada", mapper.paraDto(candidatura));
    }

    @Override
    @Transactional(readOnly = true)
    public TypedResponse<CandidaturaDTO> consultarCandidatura(ConsultarCandidaturaDTO query) {
        if (query == null || query.getUsuarioSolicitanteId() == null || query.getCandidaturaId() == null) {
            return erro(400, CONSULTA_CANDIDATURA_INVALIDA);
        }
        Candidatura candidatura = candidaturas.buscarPorId(query.getCandidaturaId()).orElse(null);
        if (candidatura == null) {
            return erro(404, CANDIDATURA_NAO_ENCONTRADA);
        }
        if (pertenceAoUsuario(candidatura, query.getUsuarioSolicitanteId())) {
            return new TypedResponse<>(200, "Candidatura encontrada", mapper.paraDtoDetalhado(candidatura));
        }
        Vaga vaga = vagas.buscarPorId(candidatura.getVagaId()).orElse(null);
        if (vaga != null && autorizacao.podeGerenciar(query.getUsuarioSolicitanteId(), vaga)) {
            return new TypedResponse<>(200, "Candidatura encontrada", mapper.paraDtoDetalhado(candidatura));
        }
        return erro(403, USUARIO_NAO_AUTORIZADO_CONSULTAR_CANDIDATURA);
    }

    private boolean pertenceAoUsuario(Candidatura candidatura, UUID usuarioId) {
        return candidatura.getUsuarioId().equals(usuarioId);
    }

    private void publicarCriacao(Candidatura candidatura) {
        quadroCache.salvar(candidatura.getVagaId(), mapper.paraDto(candidatura));
        publicadorEventos.publicarCriacao(candidatura);
    }

    private void publicarAlteracao(Candidatura candidatura, StatusCandidatura anterior) {
        publicadorEventos.publicarAlteracao(candidatura, anterior);
    }

    private TypedResponse<CandidaturaDTO> erro(int status, String mensagem) {
        return new TypedResponse<>(status, mensagem, null);
    }

}
