package br.com.pacto.recrutamento.app.ports.candidatura;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaCandidaturaRepositorio {
    Optional<PerguntaVaga> buscarAtivaPorId(UUID perguntaId);
}
