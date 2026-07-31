package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidaturas", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidaturas_candidato_vaga", columnNames = {"candidato_id", "vaga_id"}))
public class Candidatura extends EntidadeAuditavel {
    @Column(name = "candidato_id", nullable = false)
    private UUID candidatoId;
    @Column(name = "vaga_id", nullable = false)
    private UUID vagaId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusCandidatura status = StatusCandidatura.ENVIADA;
    @Column(name = "cancelado_em")
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
