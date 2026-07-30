package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RequisitoTemplateVaga extends EntidadeAuditavel {
    private UUID templateVagaId;
    private String descricao;
    private boolean obrigatorio;
    private OffsetDateTime excluidoEm;

    public RequisitoTemplateVaga() {}
    public UUID getTemplateVagaId() { return templateVagaId; }
    public void setTemplateVagaId(UUID templateVagaId) { this.templateVagaId = templateVagaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
    public void setObrigatorio(boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
