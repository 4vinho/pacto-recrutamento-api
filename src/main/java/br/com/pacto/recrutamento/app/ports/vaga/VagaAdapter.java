package br.com.pacto.recrutamento.app.ports.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.Optional;
import java.util.UUID;

public interface VagaAdapter {
    Optional<Vaga> buscarAtivaPorId(UUID id);

    Vaga salvar(Vaga vaga);
}
