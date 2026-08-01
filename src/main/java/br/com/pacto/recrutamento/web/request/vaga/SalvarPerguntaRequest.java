package br.com.pacto.recrutamento.web.request.vaga;

import br.com.pacto.recrutamento.core.enums.TipoResposta;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SalvarPerguntaRequest {
    @NotBlank public String enunciado;
    @NotNull public TipoResposta tipoResposta;
    public boolean obrigatoria;
    @Min(0) public int ordem;
}
