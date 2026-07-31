package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "candidatos", uniqueConstraints = @UniqueConstraint(name = "uk_candidatos_usuario", columnNames = "usuario_id"))
public class Candidato extends EntidadeAuditavel {
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "data_admissao")
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
