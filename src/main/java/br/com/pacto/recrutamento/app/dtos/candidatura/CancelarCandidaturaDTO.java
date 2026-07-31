package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.UUID;

public class CancelarCandidaturaDTO {
    private final UUID usuarioId;
    private final UUID candidaturaId;

    public CancelarCandidaturaDTO(UUID usuarioId, UUID candidaturaId) {
        this.usuarioId = usuarioId;
        this.candidaturaId = candidaturaId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }
}
