package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.enums.TipoResposta;
import java.util.UUID;

interface PerguntaCandidaturaProjection {
    UUID getId();
    UUID getVagaId();
    String getEnunciado();
    TipoResposta getTipoResposta();
    boolean isObrigatoria();
    int getOrdem();
}
