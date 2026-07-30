package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public abstract class EntidadeAuditavel extends Entidade {
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    protected EntidadeAuditavel() {
    }

    protected EntidadeAuditavel(UUID id) {
        super(id);
        criadoEm = OffsetDateTime.now();
        atualizadoEm = criadoEm;
    }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
