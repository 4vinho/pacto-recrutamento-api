package br.com.pacto.recrutamento.infra.candidato;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidatos", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidatos_usuario", columnNames = "usuario_id"))
public class CandidatoJpaEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;

    protected CandidatoJpaEntity() {
    }

    CandidatoJpaEntity(UUID id, UUID usuarioId, LocalDate dataAdmissao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
    }

    @PrePersist
    void prepararInclusao() {
        OffsetDateTime agora = OffsetDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void prepararAtualizacao() {
        atualizadoEm = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
}
