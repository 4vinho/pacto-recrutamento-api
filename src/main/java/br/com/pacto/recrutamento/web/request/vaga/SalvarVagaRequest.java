package br.com.pacto.recrutamento.web.request.vaga;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public class SalvarVagaRequest {
    @NotEmpty public Set<@NotNull UUID> responsaveisIds;
    @NotBlank public String titulo;
    @NotBlank public String descricao;
}
