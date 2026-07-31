package br.com.pacto.recrutamento.app.ports.usuario;

import br.com.pacto.recrutamento.core.entities.Papel;
import br.com.pacto.recrutamento.core.enums.NomePapel;

import java.util.Optional;

public interface PapelPort {
    Optional<Papel> buscarPorNome(NomePapel nome);
}
