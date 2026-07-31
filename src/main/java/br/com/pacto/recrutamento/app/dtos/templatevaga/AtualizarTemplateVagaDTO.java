package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class AtualizarTemplateVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID templateId;
    private final String titulo;
    private final String descricao;

    public AtualizarTemplateVagaDTO(UUID usuarioSolicitanteId, UUID templateId,
                                    String titulo, String descricao) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.templateId = templateId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getTemplateId() { return templateId; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
}
