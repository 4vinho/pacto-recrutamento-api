package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RespostaCandidatura {
    private UUID id;
    private UUID candidaturaId;
    private UUID perguntaId;
    private String valor;
    private OffsetDateTime criadoEm;
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
