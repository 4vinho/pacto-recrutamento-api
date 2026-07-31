package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class RequisitoVagaDTO {
    private final UUID id;
    private final String descricao;
    private final boolean obrigatorio;

    public RequisitoVagaDTO(UUID id, String descricao, boolean obrigatorio) {
        this.id = id;
        this.descricao = descricao;
        this.obrigatorio = obrigatorio;
    }

    public UUID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isObrigatorio() {
        return obrigatorio;
    }
}
