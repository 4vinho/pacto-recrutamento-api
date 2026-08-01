package br.com.pacto.recrutamento.web.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank @Email public String email;
    @NotBlank public String senha;
}
