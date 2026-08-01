package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historicos_candidatura")
public class HistoricoCandidatura {
    @Id
    private UUID id;
    @Column(name = "candidatura_id", nullable = false)
    private UUID candidaturaId;
    @Column(name = "autor_id", nullable = false)
    private UUID autorId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusCandidatura statusAnterior;
    @Enumerated(EnumType.STRING)
    @Column(name = "novo_status", nullable = false, length = 30)
    private StatusCandidatura novoStatus;
    @Column(length = 2000)
    private String feedback;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected HistoricoCandidatura() { }

    public HistoricoCandidatura(UUID candidaturaId, UUID autorId,
                                StatusCandidatura statusAnterior,
                                StatusCandidatura novoStatus, String feedback) {
        this.id = UUID.randomUUID();
        this.candidaturaId = candidaturaId;
        this.autorId = autorId;
        this.statusAnterior = statusAnterior;
        this.novoStatus = novoStatus;
        this.feedback = feedback == null ? null : feedback.trim();
        this.criadoEm = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public UUID getAutorId() { return autorId; }
    public StatusCandidatura getStatusAnterior() { return statusAnterior; }
    public StatusCandidatura getNovoStatus() { return novoStatus; }
    public String getFeedback() { return feedback; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
