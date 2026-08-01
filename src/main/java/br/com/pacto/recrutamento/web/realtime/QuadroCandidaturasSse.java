package br.com.pacto.recrutamento.web.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuadroCandidaturasSse implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuadroCandidaturasSse.class);
    private static final long TIMEOUT_MS = 30L * 60L * 1000L;
    private final ConcurrentHashMap<UUID, Set<SseEmitter>> assinantes = new ConcurrentHashMap<>();

    public SseEmitter assinar(UUID vagaId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MS);
        assinantes.computeIfAbsent(vagaId, id -> ConcurrentHashMap.newKeySet()).add(emitter);
        Runnable remover = () -> remover(vagaId, emitter);
        emitter.onCompletion(remover);
        emitter.onTimeout(remover);
        emitter.onError(error -> remover.run());
        try {
            emitter.send(SseEmitter.event().name("connected").data("{}"));
        } catch (IOException exception) {
            remover.run();
        }
        return emitter;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        UUID vagaId = extrairVagaId(payload);
        if (vagaId == null) return;
        for (SseEmitter emitter : assinantes.getOrDefault(vagaId, java.util.Collections.emptySet())) {
            try {
                emitter.send(SseEmitter.event().name("board-changed").data(payload));
            } catch (IOException | IllegalStateException exception) {
                remover(vagaId, emitter);
            }
        }
    }

    private UUID extrairVagaId(String payload) {
        String marcador = "\"vagaId\":\"";
        int inicio = payload.indexOf(marcador);
        if (inicio < 0) return null;
        inicio += marcador.length();
        int fim = payload.indexOf('"', inicio);
        try {
            return UUID.fromString(payload.substring(inicio, fim));
        } catch (RuntimeException exception) {
            LOGGER.warn("Evento Redis de candidatura invalido");
            return null;
        }
    }

    private void remover(UUID vagaId, SseEmitter emitter) {
        Set<SseEmitter> emitters = assinantes.get(vagaId);
        if (emitters == null) return;
        emitters.remove(emitter);
        if (emitters.isEmpty()) assinantes.remove(vagaId, emitters);
    }
}
