package br.com.pacto.recrutamento.app.ports.out.vaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface RequisitoVagaPort {
    List<RequisitoVaga> listarAtivosPorVagaId(UUID vagaId);

    Optional<RequisitoVaga> buscarAtivoPorId(UUID id);

    RequisitoVaga salvar(RequisitoVaga requisito);
}
