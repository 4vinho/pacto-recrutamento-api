package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.Optional;
import java.util.UUID;

public interface VagaCandidaturaPort {
    Optional<Vaga> buscarPorId(UUID vagaId);
}
