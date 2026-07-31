package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.app.candidatura.EventosCandidatura;
import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
class EventosCandidaturaSpringAdapter implements EventosCandidatura {
    private final ApplicationEventPublisher publisher;
    EventosCandidaturaSpringAdapter(ApplicationEventPublisher publisher) { this.publisher = publisher; }
    public void candidaturaCriada(Candidatura candidatura) {
        publisher.publishEvent(new CandidaturaCriadaDTO(UUID.randomUUID(), candidatura.getId(), OffsetDateTime.now()));
    }
    public void statusAlterado(Candidatura candidatura, StatusCandidatura anterior) {
        publisher.publishEvent(new StatusCandidaturaAlteradoDTO(UUID.randomUUID(), candidatura.getId(),
                anterior, candidatura.getStatus(), OffsetDateTime.now()));
    }
}
