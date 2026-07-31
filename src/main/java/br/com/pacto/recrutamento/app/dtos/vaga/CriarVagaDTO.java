package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.Collection;
import java.util.UUID;

public class CriarVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final Collection<UUID> responsaveisIds;
    private final String titulo;
    private final String descricao;

    public CriarVagaDTO(UUID usuarioSolicitanteId, Collection<UUID> responsaveisIds,
                        String titulo, String descricao) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.responsaveisIds = responsaveisIds;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
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
}
