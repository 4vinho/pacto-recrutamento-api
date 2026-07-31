package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class Entidade {
    @Id
    private UUID id;

    protected Entidade() {
        this(UUID.randomUUID());
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
