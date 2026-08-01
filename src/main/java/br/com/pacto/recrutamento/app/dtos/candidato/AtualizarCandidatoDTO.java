package br.com.pacto.recrutamento.app.dtos.candidato;

import java.util.UUID;

public class AtualizarCandidatoDTO {
    private final UUID usuarioId;
    private final String tituloProfissional, resumoProfissional, experiencia, formacao, habilidades;

    public AtualizarCandidatoDTO(UUID usuarioId, String tituloProfissional, String resumoProfissional,
                                String experiencia, String formacao, String habilidades) {
        this.usuarioId = usuarioId;
        this.tituloProfissional = tituloProfissional;
        this.resumoProfissional = resumoProfissional;
        this.experiencia = experiencia;
        this.formacao = formacao;
        this.habilidades = habilidades;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getTituloProfissional() { return tituloProfissional; }
    public String getResumoProfissional() { return resumoProfissional; }
    public String getExperiencia() { return experiencia; }
    public String getFormacao() { return formacao; }
    public String getHabilidades() { return habilidades; }
}
