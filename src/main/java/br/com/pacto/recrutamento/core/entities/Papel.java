package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.NomePapel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "papeis", uniqueConstraints = @UniqueConstraint(name = "uk_papeis_nome", columnNames = "nome"))
public class Papel extends Entidade {
    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false, length = 50)
    private NomePapel nome;

    public Papel() {}
    public Papel(UUID id, NomePapel nome) {
        super(id);
        this.nome = nome;
    }

    public NomePapel getNome() { return nome; }
    public void setNome(NomePapel nome) { this.nome = nome; }
}
