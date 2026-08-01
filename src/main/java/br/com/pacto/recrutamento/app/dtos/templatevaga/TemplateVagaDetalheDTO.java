package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.List;
import java.util.UUID;

public class TemplateVagaDetalheDTO extends TemplateVagaDTO {
    private final List<RequisitoTemplateVagaDTO> requisitos;
    private final List<PerguntaTemplateVagaDTO> perguntas;
    public TemplateVagaDetalheDTO(UUID id, UUID responsavelId, String titulo, String descricao,
            List<RequisitoTemplateVagaDTO> requisitos, List<PerguntaTemplateVagaDTO> perguntas) {
        super(id, responsavelId, titulo, descricao);
        this.requisitos = requisitos;
        this.perguntas = perguntas;
    }
    public List<RequisitoTemplateVagaDTO> getRequisitos() { return requisitos; }
    public List<PerguntaTemplateVagaDTO> getPerguntas() { return perguntas; }
}
