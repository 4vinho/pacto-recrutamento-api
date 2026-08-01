package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;
import br.com.pacto.recrutamento.core.entities.RespostaRequisitoCandidatura;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidaturaPort {
    default PaginaGenerico<Candidatura> listarPorVaga(UUID vagaId, StatusCandidatura status,
                                                       int page, int pageSize) {
        throw new UnsupportedOperationException();
    }

    Optional<Candidatura> buscarPorId(UUID candidaturaId);

    Optional<Candidatura> buscarPorIdParaAtualizacao(UUID candidaturaId);

    boolean existePorCandidatoIdEVagaId(UUID candidatoId, UUID vagaId);

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
}
