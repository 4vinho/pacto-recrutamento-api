package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class ConsultarTemplateVagaDTO {
    private final UUID usuarioId;
    private final UUID templateId;
    public ConsultarTemplateVagaDTO(UUID usuarioId, UUID templateId) {
        this.usuarioId = usuarioId;
        this.templateId = templateId;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getTemplateId() { return templateId; }
}
