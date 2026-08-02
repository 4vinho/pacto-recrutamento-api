package br.com.pacto.recrutamento.web.request.candidatura;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class EnviarCandidaturaRequest {
    @NotNull @Valid public List<RespostaRequest> respostas;
    @NotNull @Valid public List<RespostaRequisitoRequest> requisitos;
}
