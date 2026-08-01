package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "candidatos", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidatos_usuario", columnNames = "usuario_id"))
public class Candidato extends EntidadeAuditavel {
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "titulo_profissional", length = 120)
    private String tituloProfissional;
    @Column(name = "resumo_profissional", length = 1000)
    private String resumoProfissional;
    @Column(name = "experiencia", length = 2000)
    private String experiencia;
    @Column(name = "formacao", length = 1000)
    private String formacao;
    @Column(name = "habilidades", length = 500)
    private String habilidades;

    public Candidato() {
    }

    public Candidato(UUID usuarioId, String tituloProfissional, String resumoProfissional,
                     String experiencia, String formacao, String habilidades) {
        super(UUID.randomUUID());
        this.usuarioId = usuarioId;
        atualizarPerfil(tituloProfissional, resumoProfissional, experiencia, formacao, habilidades);
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void atualizarPerfil(String tituloProfissional, String resumoProfissional,
                                String experiencia, String formacao, String habilidades) {
        this.tituloProfissional = tituloProfissional;
        this.resumoProfissional = resumoProfissional;
        this.experiencia = experiencia;
        this.formacao = formacao;
        this.habilidades = habilidades;
    }
    public String getTituloProfissional() { return tituloProfissional; }
    public String getResumoProfissional() { return resumoProfissional; }
    public String getExperiencia() { return experiencia; }
    public String getFormacao() { return formacao; }
    public String getHabilidades() { return habilidades; }
}
