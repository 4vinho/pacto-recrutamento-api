package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaCandidaturaPort {
    Optional<PerguntaVaga> buscarAtivaPorId(UUID perguntaId);
}
