package br.com.pacto.recrutamento.app.ports.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.Optional;
import java.util.UUID;

public interface VagaCandidaturaAdapter {
    Optional<Vaga> buscarPorId(UUID vagaId);
}
