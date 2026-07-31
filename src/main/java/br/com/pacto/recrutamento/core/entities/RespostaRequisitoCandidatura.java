package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "respostas_requisitos_candidatura", uniqueConstraints = @UniqueConstraint(
        name = "uk_respostas_requisitos_candidatura_requisito",
        columnNames = {"candidatura_id", "requisito_id"}))
public class RespostaRequisitoCandidatura {
    @Id
    private UUID id;
    @Column(name = "candidatura_id", nullable = false)
    private UUID candidaturaId;
    @Column(name = "requisito_id", nullable = false)
    private UUID requisitoId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelAtendimentoRequisito nivel;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected RespostaRequisitoCandidatura() { }

    public RespostaRequisitoCandidatura(UUID candidaturaId, UUID requisitoId,
                                        NivelAtendimentoRequisito nivel) {
        this.id = UUID.randomUUID();
        this.candidaturaId = candidaturaId;
        this.requisitoId = requisitoId;
        this.nivel = nivel;
        this.criadoEm = OffsetDateTime.now();
        this.atualizadoEm = criadoEm;
    }

    @PrePersist
    void incluir() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (criadoEm == null) criadoEm = agora;
        atualizadoEm = agora;
    }

    public UUID getId() { return id; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public UUID getRequisitoId() { return requisitoId; }
    public NivelAtendimentoRequisito getNivel() { return nivel; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
}
