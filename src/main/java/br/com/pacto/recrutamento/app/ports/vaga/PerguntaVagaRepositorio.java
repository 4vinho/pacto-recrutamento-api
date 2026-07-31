package br.com.pacto.recrutamento.app.ports.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaVagaRepositorio {
    Optional<PerguntaVaga> buscarAtivaPorId(UUID id);
    PerguntaVaga salvar(PerguntaVaga pergunta);
}
