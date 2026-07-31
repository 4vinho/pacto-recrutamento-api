package br.com.pacto.recrutamento.app.dtos.templatevaga;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import java.util.UUID;

public class SalvarPerguntaTemplateVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID templateId;
    private final UUID perguntaId;
    private final String enunciado;
    private final TipoResposta tipoResposta;
    private final boolean obrigatoria;
    private final int ordem;

    public SalvarPerguntaTemplateVagaDTO(UUID usuarioSolicitanteId, UUID templateId,
                                         UUID perguntaId, String enunciado,
                                         TipoResposta tipoResposta, boolean obrigatoria,
                                         int ordem) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.templateId = templateId;
        this.perguntaId = perguntaId;
        this.enunciado = enunciado;
        this.tipoResposta = tipoResposta;
        this.obrigatoria = obrigatoria;
        this.ordem = ordem;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public UUID getPerguntaId() {
        return perguntaId;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public TipoResposta getTipoResposta() {
        return tipoResposta;
    }

    public boolean isObrigatoria() {
        return obrigatoria;
    }

    public int getOrdem() {
        return ordem;
    }
}
