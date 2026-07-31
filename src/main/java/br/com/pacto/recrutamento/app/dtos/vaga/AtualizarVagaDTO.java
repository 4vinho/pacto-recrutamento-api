package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.Collection;
import java.util.UUID;

public class AtualizarVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID vagaId;
    private final Collection<UUID> responsaveisIds;
    private final String titulo;
    private final String descricao;

    public AtualizarVagaDTO(UUID usuarioSolicitanteId, UUID vagaId, Collection<UUID> responsaveisIds,
                            String titulo, String descricao) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.vagaId = vagaId;
        this.responsaveisIds = responsaveisIds;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getVagaId() {
        return vagaId;
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
