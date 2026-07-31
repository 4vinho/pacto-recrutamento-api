package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.util.UUID;

public class VagaDTO {
    private final UUID id;
    private final UUID responsavelId;
    private final String titulo;
    private final String descricao;
    private final StatusVaga status;

    public VagaDTO(UUID id, UUID responsavelId, String titulo,
                   String descricao, StatusVaga status) {
        this.id = id;
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
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

    public StatusVaga getStatus() {
        return status;
    }
}
