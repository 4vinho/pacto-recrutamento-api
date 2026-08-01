package br.com.pacto.recrutamento.app.dtos.candidato;

import br.com.pacto.recrutamento.app.dtos.curriculo.CurriculoDTO;
import java.util.UUID;

public class CandidatoDTO {
    private final UUID id;
    private final UUID usuarioId;
    private final String tituloProfissional;
    private final String resumoProfissional;
    private final String experiencia;
    private final String formacao;
    private final String habilidades;
    private final CurriculoDTO curriculo;

    public CandidatoDTO(UUID id, UUID usuarioId, String tituloProfissional,
                        String resumoProfissional, String experiencia, String formacao,
                        String habilidades, CurriculoDTO curriculo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tituloProfissional = tituloProfissional;
        this.resumoProfissional = resumoProfissional;
        this.experiencia = experiencia;
        this.formacao = formacao;
        this.habilidades = habilidades;
        this.curriculo = curriculo;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTituloProfissional() { return tituloProfissional; }
    public String getResumoProfissional() { return resumoProfissional; }
    public String getExperiencia() { return experiencia; }
    public String getFormacao() { return formacao; }
    public String getHabilidades() { return habilidades; }
    public CurriculoDTO getCurriculo() { return curriculo; }
}
