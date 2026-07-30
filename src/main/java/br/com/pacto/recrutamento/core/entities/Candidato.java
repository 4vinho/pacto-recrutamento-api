package br.com.pacto.recrutamento.core.entities;

import java.time.LocalDate;
import java.util.UUID;

public class Candidato extends EntidadeAuditavel {
    private UUID usuarioId;
    private LocalDate dataAdmissao;

    public Candidato() {}
    public Candidato(UUID usuarioId, LocalDate dataAdmissao) {
        super(UUID.randomUUID());
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
}
