package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

public interface EventosCandidaturaPort {
    void candidaturaCriada(Candidatura candidatura);

    void statusAlterado(Candidatura candidatura, StatusCandidatura statusAnterior);

    default void statusAlterado(Candidatura candidatura, StatusCandidatura statusAnterior,
                                String feedback) {
        statusAlterado(candidatura, statusAnterior);
    }
}
