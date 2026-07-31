package br.com.pacto.recrutamento.infra.candidato;

import java.time.OffsetDateTime;
import java.util.UUID;

interface CandidaturaPainelProjection {
    UUID getCandidaturaId();

    UUID getVagaId();

    String getTituloVaga();

    String getStatus();

    OffsetDateTime getCriadaEm();

    String getFeedback();
}
