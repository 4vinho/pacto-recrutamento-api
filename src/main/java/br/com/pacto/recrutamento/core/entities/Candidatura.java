package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Candidatura extends EntidadeAuditavel {
    private UUID candidatoId;
    private UUID vagaId;
    private StatusCandidatura status = StatusCandidatura.ENVIADA;
    private OffsetDateTime canceladoEm;

    public Candidatura() {}
    public Candidatura(UUID candidatoId, UUID vagaId) {
        super(UUID.randomUUID());
        this.candidatoId = candidatoId;
        this.vagaId = vagaId;
    }

    public void cancelar(OffsetDateTime data) {
        status = StatusCandidatura.CANCELADA;
        canceladoEm = data;
    }
    public UUID getCandidatoId() { return candidatoId; }
    public void setCandidatoId(UUID candidatoId) { this.candidatoId = candidatoId; }
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public void setStatus(StatusCandidatura status) { this.status = status; }
    public OffsetDateTime getCanceladoEm() { return canceladoEm; }
    public void setCanceladoEm(OffsetDateTime canceladoEm) { this.canceladoEm = canceladoEm; }
}
