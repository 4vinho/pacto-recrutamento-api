package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class SalvarRequisitoTemplateVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID templateId;
    private final UUID requisitoId;
    private final String descricao;
    private final boolean obrigatorio;

    public SalvarRequisitoTemplateVagaDTO(UUID usuarioSolicitanteId, UUID templateId,
                                          UUID requisitoId, String descricao,
                                          boolean obrigatorio) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.templateId = templateId;
        this.requisitoId = requisitoId;
        this.descricao = descricao;
        this.obrigatorio = obrigatorio;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getTemplateId() { return templateId; }
    public UUID getRequisitoId() { return requisitoId; }
    public String getDescricao() { return descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
}
