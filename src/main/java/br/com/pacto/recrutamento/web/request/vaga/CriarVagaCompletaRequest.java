package br.com.pacto.recrutamento.web.request.vaga;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CriarVagaCompletaRequest {
    public Set<@NotNull UUID> responsaveisIds;
    @NotBlank public String titulo;
    @NotBlank public String descricao;
    @NotNull public List<@Valid SalvarPerguntaRequest> perguntas;
    @NotNull public List<@Valid SalvarRequisitoRequest> requisitos;
}
