package br.com.pacto.recrutamento.web.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RecuperacaoRequest {
    @NotBlank @Email public String email;
}
