package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.time.OffsetDateTime;
import java.util.UUID;

interface CandidaturaProjection {
    UUID getId();
    UUID getCandidatoId();
    UUID getVagaId();
    StatusCandidatura getStatus();
    OffsetDateTime getCriadoEm();
    OffsetDateTime getAtualizadoEm();
    OffsetDateTime getCanceladoEm();
}
