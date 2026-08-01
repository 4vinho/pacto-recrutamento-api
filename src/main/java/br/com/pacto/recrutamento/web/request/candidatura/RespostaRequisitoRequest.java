package br.com.pacto.recrutamento.web.request.candidatura;

import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class RespostaRequisitoRequest {
    @NotNull public UUID requisitoId;
    @NotNull public NivelAtendimentoRequisito nivel;
}
