package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class CriarVagaAPartirDoTemplateDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID templateId;

    public CriarVagaAPartirDoTemplateDTO(UUID usuarioSolicitanteId, UUID templateId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.templateId = templateId;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getTemplateId() {
        return templateId;
    }
}
