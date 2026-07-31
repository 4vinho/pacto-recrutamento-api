package br.com.pacto.recrutamento.infra.projections;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface CandidaturaPainelProjection {
    UUID getCandidaturaId();

    UUID getVagaId();

    String getTituloVaga();

    String getStatus();

    OffsetDateTime getCriadaEm();

    String getFeedback();
}
