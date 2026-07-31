package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class ExcluirVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID vagaId;

    public ExcluirVagaDTO(UUID usuarioSolicitanteId, UUID vagaId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.vagaId = vagaId;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getVagaId() { return vagaId; }
}
