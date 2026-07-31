package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class ExcluirItemVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID vagaId;
    private final UUID itemId;

    public ExcluirItemVagaDTO(UUID usuarioSolicitanteId, UUID vagaId, UUID itemId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.vagaId = vagaId;
        this.itemId = itemId;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getVagaId() { return vagaId; }
    public UUID getItemId() { return itemId; }
}
