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
    private final CandidaturaDtoMapper mapper;
    private final PublicadorEventosCandidatura publicadorEventos;
    private final ConsultaCandidaturaService consultas;

    public CandidaturaService(CandidaturaPort candidaturas,
                              VagaCandidaturaPort vagas,
                              PerguntaCandidaturaPort perguntas,
                              RequisitoCandidaturaPort requisitos,
                              AutorizacaoResponsavelCandidaturaPort autorizacao,
                              CandidaturaDtoMapper mapper,
                              PublicadorEventosCandidatura publicadorEventos,
                              ConsultaCandidaturaService consultas) {
        this.candidaturas = candidaturas;
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.autorizacao = autorizacao;
        this.mapper = mapper;
        this.publicadorEventos = publicadorEventos;
        this.consultas = consultas;
    }

    @Override
    @Transactional(readOnly = true)
    public TypedPagedResponse<CandidaturaDTO> listarMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        return consultas.listarMinhasCandidaturas(query);
    }

    @Override
    @Transactional(readOnly = true)
    public TypedResponse<ResumoCandidaturasDTO> resumirMinhasCandidaturas(
            ListarMinhasCandidaturasDTO query) {
        return consultas.resumirMinhasCandidaturas(query);
    }

    @Override
    @Transactional(readOnly = true)
    public TypedPagedResponse<CandidaturaDTO> listarCandidaturasDaVaga(
            ListarCandidaturasDaVagaDTO query) {
        return consultas.listarCandidaturasDaVaga(query);
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
    public TypedResponse<CandidaturaDTO> enviarCandidatura(EnviarCandidaturaDTO command) {
        if (command == null || command.getUsuarioId() == null || command.getVagaId() == null
                || command.getRespostas() == null || command.getRequisitos() == null) {
            return erro(400, DADOS_CANDIDATURA_INVALIDOS);
        }
        Vaga vaga = vagas.buscarPorId(command.getVagaId()).orElse(null);
        if (vaga == null) return erro(404, VAGA_NAO_ENCONTRADA);
        if (!vaga.aceitaCandidatura()) return erro(422, VAGA_NAO_ACEITA_CANDIDATURAS);

        Candidatura candidatura = candidaturas.buscarPorUsuarioIdEVagaIdParaAtualizacao(
                command.getUsuarioId(), command.getVagaId()).orElse(null);
        if (candidatura != null && candidatura.getStatus() != StatusCandidatura.RASCUNHO) {
            return erro(409, CANDIDATURA_DUPLICADA);
        }

        List<PerguntaVaga> perguntasDaVaga = perguntas.listarAtivasPorVagaId(vaga.getId());
        List<RequisitoVaga> requisitosDaVaga = requisitos.listarAtivosPorVagaId(vaga.getId());
        boolean nova = candidatura == null;
        if (nova) {
            candidatura = new Candidatura(command.getUsuarioId(), vaga.getId());
            candidatura.configurarEtapas(!perguntasDaVaga.isEmpty(), !requisitosDaVaga.isEmpty());
        }

        Optional<List<RespostaCandidatura>> respostasValidadas =
                ValidadorRespostasCandidatura.validarPerguntas(
                        candidatura, command.getRespostas(), perguntasDaVaga);
        if (!respostasValidadas.isPresent()) return erro(422, LOTE_RESPOSTAS_INCOMPATIVEL);
        Optional<List<RespostaRequisitoCandidatura>> requisitosValidados =
                ValidadorRespostasCandidatura.validarRequisitos(
                        candidatura, command.getRequisitos(), requisitosDaVaga);
        if (!requisitosValidados.isPresent()) return erro(422, LOTE_REQUISITOS_INCOMPATIVEL);

        if (nova) candidatura = candidaturas.salvar(candidatura);
        if (!candidatura.isPerguntasRespondidas()) {
            candidaturas.registrarRespostasPerguntasAtomicamente(
                    candidatura, respostasValidadas.get());
        }
        if (!candidatura.isRequisitosRespondidos()) {
            candidaturas.registrarRespostasRequisitosAtomicamente(
                    candidatura, requisitosValidados.get());
        }
        if (candidatura.getStatus() == StatusCandidatura.ENVIADA) publicarCriacao(candidatura);
        return new TypedResponse<>(nova ? 201 : 200, "Candidatura registrada",
                mapper.paraDto(candidatura));
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
        publicarAlteracao(candidatura, anterior, command.getFeedback());
        CandidaturaDTO atualizado = mapper.paraDto(candidatura);
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
                && candidatura.getStatus() != StatusCandidatura.TRIAGEM
                && candidatura.getStatus() != StatusCandidatura.ENTREVISTA_COMPORTAMENTAL
                && candidatura.getStatus() != StatusCandidatura.ENTREVISTA_TECNICA) {
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
        publicarAlteracao(candidatura, anterior, null);
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
            return new TypedResponse<>(200, "Candidatura encontrada",
                    mapper.paraDtoDetalhadoCandidato(candidatura));
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
        publicadorEventos.publicarCriacao(candidatura);
    }

    private void publicarAlteracao(Candidatura candidatura, StatusCandidatura anterior, String feedback) {
        publicadorEventos.publicarAlteracao(candidatura, anterior, feedback);
    }

    private TypedResponse<CandidaturaDTO> erro(int status, String mensagem) {
        return new TypedResponse<>(status, mensagem, null);
    }

}
