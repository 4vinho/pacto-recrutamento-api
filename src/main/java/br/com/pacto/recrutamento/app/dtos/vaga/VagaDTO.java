package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.util.Collection;
import java.util.UUID;

public class VagaDTO {
    private final UUID id;
    private final Collection<UUID> responsaveisIds;
    private final String titulo;
    private final String descricao;
    private final StatusVaga status;

    public VagaDTO(UUID id, Collection<UUID> responsaveisIds, String titulo,
                   String descricao, StatusVaga status) {
        this.id = id;
        this.responsaveisIds = responsaveisIds;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Collection<UUID> getResponsaveisIds() {
        return responsaveisIds;
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
