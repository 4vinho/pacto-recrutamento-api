package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "respostas_candidatura", uniqueConstraints = @UniqueConstraint(
        name = "uk_respostas_candidatura_pergunta", columnNames = {"candidatura_id", "pergunta_id"}))
public class RespostaCandidatura extends EntidadeAuditavel {
    @Column(name = "candidatura_id", nullable = false)
    private UUID candidaturaId;
    @Column(name = "pergunta_id", nullable = false)
    private UUID perguntaId;
    @Column(name = "valor", nullable = false, columnDefinition = "TEXT")
    private String valor;

    public RespostaCandidatura() {}
    public RespostaCandidatura(UUID candidaturaId, UUID perguntaId, String valor) {
        super(UUID.randomUUID());
        this.candidaturaId = candidaturaId;
        this.perguntaId = perguntaId;
        this.valor = valor;
    }

    public UUID getCandidaturaId() { return candidaturaId; }
    public void setCandidaturaId(UUID candidaturaId) { this.candidaturaId = candidaturaId; }
    public UUID getPerguntaId() { return perguntaId; }
    public void setPerguntaId(UUID perguntaId) { this.perguntaId = perguntaId; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}
