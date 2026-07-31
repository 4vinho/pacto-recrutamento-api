package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.util.UUID;

public class GerarUrlTemporariaCurriculoDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID curriculoId;

    public GerarUrlTemporariaCurriculoDTO(UUID usuarioSolicitanteId, UUID curriculoId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.curriculoId = curriculoId;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getCurriculoId() { return curriculoId; }
}
