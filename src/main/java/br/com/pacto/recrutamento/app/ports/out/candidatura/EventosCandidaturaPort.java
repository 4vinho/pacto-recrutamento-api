package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.util.List;

public interface EventosCandidaturaPort {
    void candidaturaCriada(Candidatura candidatura);

    void statusAlterado(Candidatura candidatura, StatusCandidatura statusAnterior);

    void quadroConsultado(java.util.UUID vagaId, List<Candidatura> candidaturas);
}
