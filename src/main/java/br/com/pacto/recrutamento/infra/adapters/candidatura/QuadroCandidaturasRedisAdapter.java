package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;
import br.com.pacto.recrutamento.app.ports.out.candidatura.QuadroCandidaturasCachePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class QuadroCandidaturasRedisAdapter implements QuadroCandidaturasCachePort {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuadroCandidaturasRedisAdapter.class);
    private static final Duration TTL = Duration.ofMinutes(15);
    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    QuadroCandidaturasRedisAdapter(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }

    public Optional<List<CandidaturaDTO>> buscar(UUID vagaId) {
        try {
            java.util.Map<Object, Object> valores = redis.opsForHash().entries(chave(vagaId));
            if (valores.isEmpty()) return Optional.empty();
            List<CandidaturaDTO> candidaturas = new java.util.ArrayList<>();
            for (java.util.Map.Entry<Object, Object> item : valores.entrySet()) {
                if ("_meta".equals(item.getKey().toString())) continue;
                candidaturas.add(mapper.readValue(item.getValue().toString(), CandidaturaDTO.class));
            }
            candidaturas.sort(java.util.Comparator.comparing(CandidaturaDTO::getCriadaEm).reversed());
            return Optional.of(candidaturas);
        } catch (Exception exception) {
            LOGGER.warn("Falha ao ler o cache do quadro; consultando PostgreSQL", exception);
            return Optional.empty();
        }
    }

    public void salvar(UUID vagaId, List<CandidaturaDTO> candidaturas) {
        try {
            String chave = chave(vagaId);
            redis.delete(chave);
            redis.opsForHash().put(chave, "_meta", "v1");
            for (CandidaturaDTO candidatura : candidaturas) {
                redis.opsForHash().put(chave, candidatura.getId().toString(),
                        mapper.writeValueAsString(candidatura));
            }
            redis.expire(chave, TTL);
        } catch (Exception exception) {
            LOGGER.warn("Falha ao aquecer o cache do quadro", exception);
        }
    }

    public void salvar(UUID vagaId, CandidaturaDTO candidatura) {
        try {
            String chave = chave(vagaId);
            if (!Boolean.TRUE.equals(redis.hasKey(chave))) return;
            if (candidatura.getStatus() == br.com.pacto.recrutamento.core.enums.StatusCandidatura.CANCELADA
                    || candidatura.getStatus() == br.com.pacto.recrutamento.core.enums.StatusCandidatura.RASCUNHO) {
                redis.opsForHash().delete(chave, candidatura.getId().toString());
            } else {
                redis.opsForHash().put(chave, candidatura.getId().toString(),
                        mapper.writeValueAsString(candidatura));
            }
            redis.expire(chave, TTL);
        } catch (Exception exception) {
            LOGGER.warn("Falha ao atualizar o cache do quadro", exception);
        }
    }

    private String chave(UUID vagaId) {
        return "recrutamento:vagas:" + vagaId + ":cards:v1";
    }
}
