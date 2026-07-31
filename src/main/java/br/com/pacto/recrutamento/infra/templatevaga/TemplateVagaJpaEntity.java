package br.com.pacto.recrutamento.infra.templatevaga;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "templates_vaga")
public class TemplateVagaJpaEntity {
    @Id private UUID id;
    @Column(name = "responsavel_id", nullable = false) private UUID responsavelId;
    @Column(nullable = false, length = 150) private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT") private String descricao;
    @Column(name = "criado_em", nullable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em") private OffsetDateTime excluidoEm;
    protected TemplateVagaJpaEntity() { }
    TemplateVagaJpaEntity(UUID id, UUID responsavelId, String titulo, String descricao, OffsetDateTime criadoEm, OffsetDateTime atualizadoEm, OffsetDateTime excluidoEm) { this.id=id; this.responsavelId=responsavelId; this.titulo=titulo; this.descricao=descricao; this.criadoEm=criadoEm; this.atualizadoEm=atualizadoEm; this.excluidoEm=excluidoEm; }
    @PrePersist void incluir() { OffsetDateTime agora=OffsetDateTime.now(); if(criadoEm==null) criadoEm=agora; atualizadoEm=agora; }
    @PreUpdate void atualizar() { atualizadoEm=OffsetDateTime.now(); }
    UUID getId() { return id; }
    UUID getResponsavelId() { return responsavelId; }
    String getTitulo() { return titulo; }
    String getDescricao() { return descricao; }
    OffsetDateTime getCriadoEm() { return criadoEm; }
    OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    OffsetDateTime getExcluidoEm() { return excluidoEm; }
}
