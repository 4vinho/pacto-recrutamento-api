package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;

import java.util.List;
import java.util.UUID;

public interface RequisitoCandidaturaPort {
    List<RequisitoVaga> listarAtivosPorVagaId(UUID vagaId);
}
