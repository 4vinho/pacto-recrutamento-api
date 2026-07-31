package br.com.pacto.recrutamento.app.ports.curriculo;

import br.com.pacto.recrutamento.core.entities.Curriculo;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface CurriculoRepositorio {
    Optional<Curriculo> buscarAtivoPorCandidato(UUID candidatoId);
    Optional<Curriculo> buscarAtivoPorId(UUID curriculoId);
    void salvar(Curriculo curriculo);
    void substituir(Curriculo anterior, Curriculo novo, OffsetDateTime excluidoEm);
}
