package br.com.pacto.recrutamento.app.ports.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.entities.RespostaCandidatura;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidaturaRepositorio {
    Optional<Candidatura> buscarPorId(UUID candidaturaId);

    boolean existePorCandidatoIdEVagaId(UUID candidatoId, UUID vagaId);

    Candidatura salvar(Candidatura candidatura);

    void salvarRespostasAtomicamente(List<RespostaCandidatura> respostas);

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
}
