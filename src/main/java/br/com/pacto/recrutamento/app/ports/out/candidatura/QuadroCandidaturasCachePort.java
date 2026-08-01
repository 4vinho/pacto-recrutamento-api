package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.app.dtos.candidatura.CandidaturaDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuadroCandidaturasCachePort {
    Optional<List<CandidaturaDTO>> buscar(UUID vagaId);
    void salvar(UUID vagaId, List<CandidaturaDTO> candidaturas);
    void salvar(UUID vagaId, CandidaturaDTO candidatura);
}
