package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.util.UUID;

public class GerarUrlTemporariaCurriculoDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID candidaturaId;

    public GerarUrlTemporariaCurriculoDTO(UUID usuarioSolicitanteId, UUID candidaturaId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.candidaturaId = candidaturaId;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }
}
