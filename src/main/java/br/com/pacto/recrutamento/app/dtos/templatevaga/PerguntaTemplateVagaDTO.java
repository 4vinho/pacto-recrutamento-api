package br.com.pacto.recrutamento.app.dtos.templatevaga;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import java.util.UUID;

public class PerguntaTemplateVagaDTO {
    private final UUID id;
    private final String enunciado;
    private final TipoResposta tipoResposta;
    private final boolean obrigatoria;
    private final int ordem;

    public PerguntaTemplateVagaDTO(UUID id, String enunciado, TipoResposta tipoResposta,
                                   boolean obrigatoria, int ordem) {
        this.id = id;
        this.enunciado = enunciado;
        this.tipoResposta = tipoResposta;
        this.obrigatoria = obrigatoria;
        this.ordem = ordem;
    }

    public UUID getId() { return id; }
    public String getEnunciado() { return enunciado; }
    public TipoResposta getTipoResposta() { return tipoResposta; }
    public boolean isObrigatoria() { return obrigatoria; }
    public int getOrdem() { return ordem; }
}
