package br.com.pacto.recrutamento.web.request.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AtualizarStatusCandidaturaRequest {
    @NotNull public StatusCandidatura status;
    @NotNull public Long versao;
    @Size(max = 2000) public String feedback;
}
