package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "respostas_candidatura", uniqueConstraints = @UniqueConstraint(
        name = "uk_respostas_candidatura_pergunta", columnNames = {"candidatura_id", "pergunta_id"}))
public class RespostaCandidatura {
    @Id
    private UUID id;
    @Column(name = "candidatura_id", nullable = false)
    private UUID candidaturaId;
    @Column(name = "pergunta_id", nullable = false)
    private UUID perguntaId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String valor;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    public RespostaCandidatura() {
        id = UUID.randomUUID();
        criadoEm = OffsetDateTime.now();
        atualizadoEm = criadoEm;
    }
    public RespostaCandidatura(UUID candidaturaId, UUID perguntaId, String valor) {
        this();
        this.candidaturaId = candidaturaId;
        this.perguntaId = perguntaId;
        this.valor = valor;
    }

    @PrePersist
    void incluir() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (criadoEm == null) {
            criadoEm = agora;
        }
        atualizadoEm = agora;
    }

    @PreUpdate
    void atualizar() {
        atualizadoEm = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public void setCandidaturaId(UUID candidaturaId) { this.candidaturaId = candidaturaId; }
    public UUID getPerguntaId() { return perguntaId; }
    public void setPerguntaId(UUID perguntaId) { this.perguntaId = perguntaId; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
