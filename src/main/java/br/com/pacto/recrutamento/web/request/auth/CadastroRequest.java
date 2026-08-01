package br.com.pacto.recrutamento.web.request.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CadastroRequest {
    @NotBlank @Size(max = 120) public String nome;
    @NotBlank @Email public String email;
    @NotBlank public String telefone;
    @NotBlank @Size(min = 8, max = 72) public String senha;
}
