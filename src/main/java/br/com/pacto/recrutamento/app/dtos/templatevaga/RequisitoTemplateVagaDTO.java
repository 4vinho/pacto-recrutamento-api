package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class RequisitoTemplateVagaDTO {
    private final UUID id;
    private final String descricao;
    private final boolean obrigatorio;

    public RequisitoTemplateVagaDTO(UUID id, String descricao, boolean obrigatorio) {
        this.id = id;
        this.descricao = descricao;
        this.obrigatorio = obrigatorio;
    }

    public UUID getId() { return id; }
    public String getDescricao() { return descricao; }
    public boolean isObrigatorio() { return obrigatorio; }
}
