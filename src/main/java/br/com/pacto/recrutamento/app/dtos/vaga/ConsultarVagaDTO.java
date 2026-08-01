package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class ConsultarVagaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;

    public ConsultarVagaDTO(UUID usuarioId, UUID vagaId) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
}
