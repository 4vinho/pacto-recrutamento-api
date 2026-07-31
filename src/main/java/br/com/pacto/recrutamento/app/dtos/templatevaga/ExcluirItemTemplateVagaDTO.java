package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class ExcluirItemTemplateVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID templateId;
    private final UUID itemId;

    public ExcluirItemTemplateVagaDTO(UUID usuarioSolicitanteId, UUID templateId, UUID itemId) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.templateId = templateId;
        this.itemId = itemId;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public UUID getItemId() {
        return itemId;
    }
}
