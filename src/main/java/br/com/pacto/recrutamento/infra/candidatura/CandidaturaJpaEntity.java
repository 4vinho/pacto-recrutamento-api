package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidaturas", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidaturas_candidato_vaga", columnNames = {"candidato_id", "vaga_id"}))
public class CandidaturaJpaEntity {
    @Id private UUID id;
    @Column(name = "candidato_id", nullable = false) private UUID candidatoId;
    @Column(name = "vaga_id", nullable = false) private UUID vagaId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private StatusCandidatura status;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "cancelado_em") private OffsetDateTime canceladoEm;

    protected CandidaturaJpaEntity() {}
    CandidaturaJpaEntity(UUID id, UUID candidatoId, UUID vagaId, StatusCandidatura status,
                         OffsetDateTime criadoEm, OffsetDateTime atualizadoEm, OffsetDateTime canceladoEm) {
        this.id = id; this.candidatoId = candidatoId; this.vagaId = vagaId; this.status = status;
        this.criadoEm = criadoEm; this.atualizadoEm = atualizadoEm; this.canceladoEm = canceladoEm;
    }
    @PrePersist void incluir() { OffsetDateTime agora = OffsetDateTime.now(); if (criadoEm == null) criadoEm = agora; atualizadoEm = agora; }
    @PreUpdate void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    public UUID getId() { return id; }
    public UUID getCandidatoId() { return candidatoId; }
    public UUID getVagaId() { return vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public OffsetDateTime getCanceladoEm() { return canceladoEm; }
}
