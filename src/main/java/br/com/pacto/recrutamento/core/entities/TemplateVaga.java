package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "templates_vaga")
public class TemplateVaga extends EntidadeAuditavel {
    @Column(name = "responsavel_id", nullable = false)
    private UUID responsavelId;
    @Column(nullable = false, length = 150)
    private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public TemplateVaga() {
    }

    public TemplateVaga(UUID responsavelId, String titulo, String descricao) {
        super(UUID.randomUUID());
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(UUID responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public OffsetDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(OffsetDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
