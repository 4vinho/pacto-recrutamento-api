package br.com.pacto.recrutamento.app.dtos.notificacao;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CandidaturaCriadaDTO {
    private final UUID eventoId;
    private final UUID candidaturaId;
    private final OffsetDateTime ocorridaEm;

    public CandidaturaCriadaDTO(UUID eventoId, UUID candidaturaId, OffsetDateTime ocorridaEm) {
        this.eventoId = eventoId;
        this.candidaturaId = candidaturaId;
        this.ocorridaEm = ocorridaEm;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }

    public OffsetDateTime getOcorridaEm() {
        return ocorridaEm;
    }
}
