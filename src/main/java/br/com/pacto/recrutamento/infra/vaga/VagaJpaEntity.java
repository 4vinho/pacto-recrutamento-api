package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vagas")
public class VagaJpaEntity {
    @Id private UUID id;
    @Column(name = "responsavel_id", nullable = false) private UUID responsavelId;
    @Column(nullable = false, length = 150) private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT") private String descricao;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private StatusVaga status;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em") private OffsetDateTime excluidoEm;

    protected VagaJpaEntity() {}

    public VagaJpaEntity(UUID id, UUID responsavelId, String titulo, String descricao, StatusVaga status,
                         OffsetDateTime criadoEm, OffsetDateTime atualizadoEm, OffsetDateTime excluidoEm) {
        this.id = id; this.responsavelId = responsavelId; this.titulo = titulo;
        this.descricao = descricao; this.status = status; this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm; this.excluidoEm = excluidoEm;
    }

    @PrePersist void incluir() { OffsetDateTime agora = OffsetDateTime.now(); if (criadoEm == null) criadoEm = agora; atualizadoEm = agora; }
    @PreUpdate void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    public UUID getId() { return id; }
    public UUID getResponsavelId() { return responsavelId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public StatusVaga getStatus() { return status; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
}
