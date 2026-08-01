package br.com.pacto.recrutamento.app.usecases.candidatura;

import br.com.pacto.recrutamento.app.ports.out.candidatura.EventosCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.stereotype.Component;


@Component
class PublicadorEventosCandidatura {
    private final EventosCandidaturaPort eventos;

    PublicadorEventosCandidatura(EventosCandidaturaPort eventos) {
        this.eventos = eventos;
    }

    void publicarCriacao(Candidatura candidatura) {
        try {
            eventos.candidaturaCriada(candidatura);
        } catch (RuntimeException ignored) {
            // O evento e posterior a confirmacao da candidatura e nao a desfaz.
        }
    }

    void publicarAlteracao(Candidatura candidatura, StatusCandidatura anterior) {
        try {
            eventos.statusAlterado(candidatura, anterior);
        } catch (RuntimeException ignored) {
            // O evento e posterior a confirmacao da alteracao e nao a desfaz.
        }
    }
}
