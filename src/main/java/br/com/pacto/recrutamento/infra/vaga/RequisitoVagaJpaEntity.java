package br.com.pacto.recrutamento.infra.vaga;

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
public class RequisitoVagaJpaEntity {
    @Id private UUID id;
    @Column(name = "vaga_id", nullable = false) private UUID vagaId;
    @Column(nullable = false, columnDefinition = "TEXT") private String descricao;
    @Column(nullable = false) private boolean obrigatorio;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em") private OffsetDateTime excluidoEm;

    protected RequisitoVagaJpaEntity() {}
    RequisitoVagaJpaEntity(UUID id, UUID vagaId, String descricao, boolean obrigatorio,
                           OffsetDateTime criadoEm, OffsetDateTime atualizadoEm, OffsetDateTime excluidoEm) {
        this.id = id; this.vagaId = vagaId; this.descricao = descricao; this.obrigatorio = obrigatorio;
        this.criadoEm = criadoEm; this.atualizadoEm = atualizadoEm; this.excluidoEm = excluidoEm;
    }
    @PrePersist void incluir() { OffsetDateTime agora = OffsetDateTime.now(); if (criadoEm == null) criadoEm = agora; atualizadoEm = agora; }
    @PreUpdate void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    UUID getId() { return id; }
    UUID getVagaId() { return vagaId; }
    String getDescricao() { return descricao; }
    boolean isObrigatorio() { return obrigatorio; }
    OffsetDateTime getCriadoEm() { return criadoEm; }
    OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    OffsetDateTime getExcluidoEm() { return excluidoEm; }
}
