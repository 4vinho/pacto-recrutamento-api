package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import br.com.pacto.recrutamento.core.entities.HistoricoCandidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import br.com.pacto.recrutamento.core.common.FiltroRespostaCandidatura;
import java.time.OffsetDateTime;
import java.util.Map;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidaturaPort {
    default PaginaGenerico<Candidatura> listarPorVaga(UUID vagaId, StatusCandidatura status,
                                                       int page, int pageSize) {
        throw new UnsupportedOperationException();
    }
    PaginaGenerico<Candidatura> listarPorUsuario(UUID usuarioId, int page, int pageSize);

    default PaginaGenerico<Candidatura> listarPorUsuario(UUID usuarioId,
            StatusCandidatura status, OffsetDateTime inicio, OffsetDateTime fim,
            int page, int pageSize) {
        return listarPorUsuario(usuarioId, page, pageSize);
    }

    default PaginaGenerico<Candidatura> listarPorVaga(UUID vagaId, StatusCandidatura status,
            String busca, List<FiltroRespostaCandidatura> filtrosRespostas, UUID requisitoId,
            NivelAtendimentoRequisito nivelMinimo, Boolean atendeTodosRequisitos,
            int page, int pageSize) {
        return listarPorVaga(vagaId, status, page, pageSize);
    }

    default Map<StatusCandidatura, Long> contarPorStatusDoUsuario(UUID usuarioId,
            OffsetDateTime inicio, OffsetDateTime fim) {
        return java.util.Collections.emptyMap();
    }

    Optional<Candidatura> buscarPorId(UUID candidaturaId);

    Optional<Candidatura> buscarPorIdParaAtualizacao(UUID candidaturaId);

    default List<Candidatura> listarCancelaveisPorVaga(UUID vagaId) {
        return java.util.Collections.emptyList();
    }

    boolean existePorUsuarioIdEVagaId(UUID usuarioId, UUID vagaId);

    default List<RespostaCandidatura> listarRespostas(UUID candidaturaId) {
        return java.util.Collections.emptyList();
    }

    default List<RespostaRequisitoCandidatura> listarRespostasRequisitos(UUID candidaturaId) {
        return java.util.Collections.emptyList();
    }

    default List<HistoricoCandidatura> listarHistorico(UUID candidaturaId) {
        return java.util.Collections.emptyList();
    }

    default void salvarHistorico(HistoricoCandidatura historico) { }

    Candidatura salvar(Candidatura candidatura);

    void registrarRespostasPerguntasAtomicamente(Candidatura candidatura,
                                                  List<RespostaCandidatura> respostas);

    void registrarRespostasRequisitosAtomicamente(Candidatura candidatura,
            List<RespostaRequisitoCandidatura> respostas);

    final class CandidaturaDuplicadaException extends RuntimeException {
        public CandidaturaDuplicadaException() {
            super("Candidatura duplicada");
        }
    }

    final class RespostasDuplicadasException extends RuntimeException {
        public RespostasDuplicadasException() {
            super("Resposta duplicada");
        }
    }

    final class RequisitosJaRespondidosException extends RuntimeException {
        public RequisitosJaRespondidosException() { super("Requisito ja respondido"); }
    }

    final class ConcorrenciaException extends RuntimeException {
        public ConcorrenciaException() { super("Candidatura atualizada por outro usuario"); }
    }
}
