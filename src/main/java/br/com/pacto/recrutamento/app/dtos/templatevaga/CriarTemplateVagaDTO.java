package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class CriarTemplateVagaDTO {
    private final UUID responsavelId;
    private final String titulo;
    private final String descricao;

    public CriarTemplateVagaDTO(UUID responsavelId, String titulo, String descricao) {
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getResponsavelId() { return responsavelId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}
