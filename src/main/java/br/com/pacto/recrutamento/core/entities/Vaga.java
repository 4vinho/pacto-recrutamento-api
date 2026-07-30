package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Vaga extends EntidadeAuditavel {
    private UUID responsavelId;
    private String titulo;
    private String descricao;
    private StatusVaga status = StatusVaga.RASCUNHO;
    private OffsetDateTime excluidoEm;

    public Vaga() {}
    public Vaga(UUID responsavelId, String titulo, String descricao) {
        super(UUID.randomUUID());
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public boolean aceitaCandidatura() {
        return status == StatusVaga.PUBLICADA && excluidoEm == null;
    }
    public UUID getResponsavelId() { return responsavelId; }
    public void setResponsavelId(UUID responsavelId) { this.responsavelId = responsavelId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusVaga getStatus() { return status; }
    public void setStatus(StatusVaga status) { this.status = status; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
