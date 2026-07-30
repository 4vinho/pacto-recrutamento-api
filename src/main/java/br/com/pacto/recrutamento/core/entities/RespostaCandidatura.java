package br.com.pacto.recrutamento.core.entities;

import java.util.UUID;

public class RespostaCandidatura extends EntidadeAuditavel {
    private UUID candidaturaId;
    private UUID perguntaId;
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
