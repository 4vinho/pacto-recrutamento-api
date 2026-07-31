package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "requisitos_template_vaga")
public class RequisitoTemplateVaga extends EntidadeAuditavel {
    @Column(name = "template_vaga_id", nullable = false)
    private UUID templateVagaId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Column(nullable = false)
    private boolean obrigatorio;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;
    public UUID getTemplateVagaId() { return templateVagaId; }
    public void setTemplateVagaId(UUID templateVagaId) { this.templateVagaId = templateVagaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
    public void setObrigatorio(boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
