package br.com.pacto.recrutamento.infra.projections;

import java.time.OffsetDateTime;
public interface CandidaturaPainelProjection {
    String getCandidaturaId();

    String getVagaId();

    String getTituloVaga();

    String getStatus();

    OffsetDateTime getCriadaEm();

    String getFeedback();
}
