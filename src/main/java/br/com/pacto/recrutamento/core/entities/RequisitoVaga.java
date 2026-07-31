package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "requisitos_vaga")
public class RequisitoVaga extends EntidadeAuditavel {
    @Column(name = "vaga_id", nullable = false)
    private UUID vagaId;
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Column(name = "obrigatorio", nullable = false)
    private boolean obrigatorio;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public RequisitoVaga() {}
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
    public void setObrigatorio(boolean obrigatorio) { this.obrigatorio = obrigatorio; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
