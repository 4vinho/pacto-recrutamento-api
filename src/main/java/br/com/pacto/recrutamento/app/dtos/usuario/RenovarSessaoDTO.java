package br.com.pacto.recrutamento.app.dtos.usuario;

public class RenovarSessaoDTO {
    private final String refreshToken;

    public RenovarSessaoDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
