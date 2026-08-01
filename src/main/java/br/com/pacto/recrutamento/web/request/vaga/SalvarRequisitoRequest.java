package br.com.pacto.recrutamento.web.request.vaga;

import javax.validation.constraints.NotBlank;

public class SalvarRequisitoRequest {
    @NotBlank public String descricao;
    public boolean obrigatorio;
}
