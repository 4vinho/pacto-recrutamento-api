package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.NomePapel;

import java.util.UUID;

public class Papel extends Entidade {
    private NomePapel nome;

    public Papel() {}
    public Papel(UUID id, NomePapel nome) {
        super(id);
        this.nome = nome;
    }

    public NomePapel getNome() { return nome; }
    public void setNome(NomePapel nome) { this.nome = nome; }
}
