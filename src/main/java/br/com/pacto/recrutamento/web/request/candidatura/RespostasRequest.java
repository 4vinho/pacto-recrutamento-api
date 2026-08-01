package br.com.pacto.recrutamento.web.request.candidatura;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class RespostasRequest {
    @NotEmpty @Valid public List<RespostaRequest> respostas;
}
