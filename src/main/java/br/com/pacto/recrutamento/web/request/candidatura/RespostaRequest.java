package br.com.pacto.recrutamento.web.request.candidatura;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class RespostaRequest {
    @NotNull public UUID perguntaId;
    @NotBlank public String valor;
}
