package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.util.UUID;

public class AlterarStatusVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID vagaId;
    private final StatusVaga status;

    public AlterarStatusVagaDTO(UUID usuarioSolicitanteId, UUID vagaId, StatusVaga status) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.vagaId = vagaId;
        this.status = status;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public StatusVaga getStatus() {
        return status;
    }
}
