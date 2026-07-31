package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;

import java.util.UUID;

public class RespostaRequisitoCandidaturaDTO {
    private final UUID requisitoId;
    private final NivelAtendimentoRequisito nivel;

    public RespostaRequisitoCandidaturaDTO(UUID requisitoId, NivelAtendimentoRequisito nivel) {
        this.requisitoId = requisitoId;
        this.nivel = nivel;
    }

    public UUID getRequisitoId() { return requisitoId; }
    public NivelAtendimentoRequisito getNivel() { return nivel; }
}
