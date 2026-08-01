package br.com.pacto.recrutamento.app.ports.out.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.common.PaginaGenerico;
import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.util.Optional;
import java.util.UUID;

public interface VagaPort {
    PaginaGenerico<Vaga> listar(String busca, StatusVaga status, int page, int pageSize,
                                String ordenarPor, boolean ascendente,
                                UUID excluirCandidaturasDoUsuarioId);

    Optional<Vaga> buscarAtivaPorId(UUID id);

    Vaga salvar(Vaga vaga);
}
