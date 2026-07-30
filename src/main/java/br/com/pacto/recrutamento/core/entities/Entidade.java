package br.com.pacto.recrutamento.core.entities;

import java.util.UUID;

public abstract class Entidade {
    private UUID id;

    protected Entidade() {
    }

    protected Entidade(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
