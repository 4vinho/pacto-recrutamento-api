package br.com.pacto.recrutamento.app.ports.out.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface PerguntaVagaPort {
    List<PerguntaVaga> listarAtivasPorVagaId(UUID vagaId);

    Optional<PerguntaVaga> buscarAtivaPorId(UUID id);

    PerguntaVaga salvar(PerguntaVaga pergunta);
}
