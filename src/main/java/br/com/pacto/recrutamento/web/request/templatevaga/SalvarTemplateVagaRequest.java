package br.com.pacto.recrutamento.web.request.templatevaga;

import javax.validation.constraints.NotBlank;

public class SalvarTemplateVagaRequest {
    @NotBlank public String titulo;
    @NotBlank public String descricao;
}
