package br.com.pacto.recrutamento.web.request.auth;

import javax.validation.constraints.NotBlank;

public class TokenRequest {
    @NotBlank public String refreshToken;
}
