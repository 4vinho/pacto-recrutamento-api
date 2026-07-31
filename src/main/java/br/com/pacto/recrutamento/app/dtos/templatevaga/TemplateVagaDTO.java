package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class TemplateVagaDTO {
    private final UUID id;
    private final UUID responsavelId;
    private final String titulo;
    private final String descricao;

    public TemplateVagaDTO(UUID id, UUID responsavelId, String titulo, String descricao) {
        this.id = id;
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getResponsavelId() {
        return responsavelId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}
