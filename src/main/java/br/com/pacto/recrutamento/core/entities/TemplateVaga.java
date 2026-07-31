package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TemplateVaga {
    private UUID id = UUID.randomUUID();
    private UUID responsavelId;
    private String titulo;
    private String descricao;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime excluidoEm;

    public TemplateVaga() { }

    public TemplateVaga(UUID responsavelId, String titulo, String descricao) {
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResponsavelId() { return responsavelId; }
    public void setResponsavelId(UUID responsavelId) { this.responsavelId = responsavelId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
