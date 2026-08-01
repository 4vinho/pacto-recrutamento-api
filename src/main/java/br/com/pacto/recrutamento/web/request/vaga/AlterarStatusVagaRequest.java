package br.com.pacto.recrutamento.web.request.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;
import javax.validation.constraints.NotNull;

public class AlterarStatusVagaRequest {
    @NotNull public StatusVaga status;
}
