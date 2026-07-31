package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.UUID;

public class CriarCandidaturaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;

    public CriarCandidaturaDTO(UUID usuarioId, UUID vagaId) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
}
