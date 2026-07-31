package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "requisitos_vaga")
public class RequisitoVaga {
    @Id private UUID id = UUID.randomUUID();
    @Column(name = "vaga_id", nullable = false) private UUID vagaId;
    @Column(nullable = false, columnDefinition = "TEXT") private String descricao;
    @Column(nullable = false) private boolean obrigatorio;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em") private OffsetDateTime excluidoEm;

    @PrePersist
    void incluir() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (criadoEm == null) criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void atualizar() { atualizadoEm = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
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
