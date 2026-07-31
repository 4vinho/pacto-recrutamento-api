package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RequisitoTemplateVaga {
    private UUID id = UUID.randomUUID();
    private UUID templateVagaId;
    private String descricao;
    private boolean obrigatorio;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime excluidoEm;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTemplateVagaId() { return templateVagaId; }
    public void setTemplateVagaId(UUID templateVagaId) { this.templateVagaId = templateVagaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
    public void setObrigatorio(boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
