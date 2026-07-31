package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.OffsetDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class EntidadeAuditavel extends Entidade {
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected EntidadeAuditavel() {
    }

    protected EntidadeAuditavel(UUID id) {
        super(id);
        inicializarAuditoria();
    }

    @PrePersist
    protected void inicializarAuditoria() {
        if (criadoEm == null) {
            criadoEm = OffsetDateTime.now();
        }
        atualizadoEm = criadoEm;
    }

    @PreUpdate
    protected void atualizarAuditoria() {
        atualizadoEm = OffsetDateTime.now();
    }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
