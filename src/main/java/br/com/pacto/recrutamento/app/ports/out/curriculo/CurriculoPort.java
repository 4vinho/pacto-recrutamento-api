package br.com.pacto.recrutamento.app.ports.out.curriculo;

import br.com.pacto.recrutamento.core.entities.Curriculo;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CurriculoPort {
    Optional<Curriculo> buscarAtivoPorCandidatura(UUID candidaturaId);

    Optional<Curriculo> buscarAtivoPorId(UUID curriculoId);

    void salvar(Curriculo curriculo);

    void substituir(Curriculo anterior, Curriculo novo, OffsetDateTime excluidoEm);
}
