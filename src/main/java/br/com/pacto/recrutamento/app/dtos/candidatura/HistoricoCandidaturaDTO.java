package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.time.OffsetDateTime;
import java.util.UUID;

public class HistoricoCandidaturaDTO {
    private final UUID id;
    private final StatusCandidatura statusAnterior;
    private final StatusCandidatura novoStatus;
    private final String feedback;
    private final OffsetDateTime criadoEm;

    public HistoricoCandidaturaDTO(UUID id, StatusCandidatura statusAnterior,
            StatusCandidatura novoStatus, String feedback, OffsetDateTime criadoEm) {
        this.id = id;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.feedback = feedback;
        this.criadoEm = criadoEm;
    }
    public UUID getId() { return id; }
    public StatusCandidatura getStatusAnterior() { return statusAnterior; }
    public StatusCandidatura getNovoStatus() { return novoStatus; }
    public String getFeedback() { return feedback; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
