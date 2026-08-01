package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.dtos.notificacao.CandidaturaCriadaDTO;
import br.com.pacto.recrutamento.app.dtos.notificacao.StatusCandidaturaAlteradoDTO;
import br.com.pacto.recrutamento.app.ports.out.candidatura.EventosCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Candidatura;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import java.time.Duration;

@Component
class EventosCandidaturaSpringAdapter implements EventosCandidaturaPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventosCandidaturaSpringAdapter.class);
    private static final String CHANNEL = "recrutamento:candidaturas:alteracoes";
    private static final Duration BOARD_TTL = Duration.ofHours(6);
    private final ApplicationEventPublisher publisher;
    private final StringRedisTemplate redis;

    EventosCandidaturaSpringAdapter(ApplicationEventPublisher publisher, StringRedisTemplate redis) {
        this.publisher = publisher;
        this.redis = redis;
    }

    public void candidaturaCriada(Candidatura candidatura) {
        atualizarQuadro(candidatura, true);
        publisher.publishEvent(new CandidaturaCriadaDTO(UUID.randomUUID(), candidatura.getId(), OffsetDateTime.now()));
    }

    public void statusAlterado(Candidatura candidatura, StatusCandidatura anterior) {
        atualizarQuadro(candidatura, true);
        publisher.publishEvent(new StatusCandidaturaAlteradoDTO(UUID.randomUUID(), candidatura.getId(),
                anterior, candidatura.getStatus(), OffsetDateTime.now()));
    }

    public void quadroConsultado(UUID vagaId, List<Candidatura> candidaturas) {
        String chave = "recrutamento:vagas:" + vagaId + ":quadro";
        try {
            redis.delete(chave);
            for (Candidatura candidatura : candidaturas) atualizarQuadro(candidatura, false);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponivel durante a carga do quadro", exception);
        }
    }

    private void atualizarQuadro(Candidatura candidatura, boolean publicar) {
        String chave = "recrutamento:vagas:" + candidatura.getVagaId() + ":quadro";
        String campo = candidatura.getId().toString();
        String evento = "{\"candidaturaId\":\"" + candidatura.getId()
                + "\",\"vagaId\":\"" + candidatura.getVagaId()
                + "\",\"status\":\"" + candidatura.getStatus()
                + "\",\"versao\":" + candidatura.getVersao() + "}";
        try {
            if (candidatura.getStatus() == StatusCandidatura.CANCELADA
                    || candidatura.getStatus() == StatusCandidatura.RASCUNHO) {
                redis.opsForHash().delete(chave, campo);
            } else {
                redis.opsForHash().put(chave, campo, evento);
            }
            redis.expire(chave, BOARD_TTL);
            if (publicar) redis.convertAndSend(CHANNEL, evento);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis indisponivel; a alteracao permanece persistida no PostgreSQL", exception);
        }
    }
}
