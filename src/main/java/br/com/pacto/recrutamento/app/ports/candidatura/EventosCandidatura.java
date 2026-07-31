package br.com.pacto.recrutamento.app.ports.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

public interface EventosCandidatura {
    void candidaturaCriada(Candidatura candidatura);

    void statusAlterado(Candidatura candidatura, StatusCandidatura statusAnterior);
}
