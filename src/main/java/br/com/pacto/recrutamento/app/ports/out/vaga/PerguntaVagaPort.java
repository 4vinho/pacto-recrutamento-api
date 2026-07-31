package br.com.pacto.recrutamento.app.ports.out.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaVagaPort {
    Optional<PerguntaVaga> buscarAtivaPorId(UUID id);

    PerguntaVaga salvar(PerguntaVaga pergunta);
}
