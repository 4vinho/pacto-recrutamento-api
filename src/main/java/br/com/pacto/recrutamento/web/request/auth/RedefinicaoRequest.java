package br.com.pacto.recrutamento.web.request.auth;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RedefinicaoRequest {
    @NotBlank public String token;
    @NotBlank @Size(min = 8, max = 72) public String novaSenha;
}
