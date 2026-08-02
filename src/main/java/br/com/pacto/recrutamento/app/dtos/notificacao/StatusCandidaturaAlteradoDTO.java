package br.com.pacto.recrutamento.app.dtos.notificacao;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class StatusCandidaturaAlteradoDTO {
    private final UUID eventoId;
    private final UUID candidaturaId;
    private final StatusCandidatura statusAnterior;
    private final StatusCandidatura novoStatus;
    private final OffsetDateTime ocorridaEm;
    private final String feedback;

    public StatusCandidaturaAlteradoDTO(UUID eventoId, UUID candidaturaId,
                                        StatusCandidatura statusAnterior,
                                        StatusCandidatura novoStatus,
                                        OffsetDateTime ocorridaEm) {
        this(eventoId, candidaturaId, statusAnterior, novoStatus, ocorridaEm, null);
    }

    public StatusCandidaturaAlteradoDTO(UUID eventoId, UUID candidaturaId,
                                        StatusCandidatura statusAnterior,
                                        StatusCandidatura novoStatus,
                                        OffsetDateTime ocorridaEm, String feedback) {
        this.eventoId = eventoId;
        this.candidaturaId = candidaturaId;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.ocorridaEm = ocorridaEm;
        this.feedback = feedback;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }

    public StatusCandidatura getStatusAnterior() {
        return statusAnterior;
    }

    public StatusCandidatura getNovoStatus() {
        return novoStatus;
    }

    public OffsetDateTime getOcorridaEm() {
        return ocorridaEm;
    }

    public String getFeedback() {
        return feedback;
    }
}
