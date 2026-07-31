package br.com.pacto.recrutamento.app.ports.vaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;

import java.util.Optional;
import java.util.UUID;

public interface RequisitoVagaAdapter {
    Optional<RequisitoVaga> buscarAtivoPorId(UUID id);

    RequisitoVaga salvar(RequisitoVaga requisito);
}
