package br.com.pacto.recrutamento.infra.projections;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import java.util.UUID;

public interface PerguntaCandidaturaProjection {
    UUID getId();

    UUID getVagaId();

    String getEnunciado();

    TipoResposta getTipoResposta();

    boolean isObrigatoria();

    int getOrdem();
}
