package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.UUID;

public class ConsultarCandidaturaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID candidaturaId;

    public ConsultarCandidaturaDTO(UUID usuarioSolicitanteId, UUID candidaturaId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.candidaturaId = candidaturaId;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getCandidaturaId() { return candidaturaId; }
}
