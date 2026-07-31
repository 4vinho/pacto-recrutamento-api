package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class CriarVagaDTO {
    private final UUID responsavelId;
    private final String titulo;
    private final String descricao;

    public CriarVagaDTO(UUID responsavelId, String titulo, String descricao) {
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
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
