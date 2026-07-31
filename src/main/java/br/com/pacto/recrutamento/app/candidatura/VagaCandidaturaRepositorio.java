package br.com.pacto.recrutamento.app.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.Optional;
import java.util.UUID;

public interface VagaCandidaturaRepositorio {
    Optional<Vaga> buscarPorId(UUID vagaId);
}
