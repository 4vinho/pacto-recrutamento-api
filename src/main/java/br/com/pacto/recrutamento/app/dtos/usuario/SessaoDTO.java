package br.com.pacto.recrutamento.app.dtos.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;

public class SessaoDTO {
    private final String accessToken;
    private final String refreshToken;
    private final OffsetDateTime accessTokenExpiraEm;

    public SessaoDTO(String accessToken, String refreshToken, OffsetDateTime accessTokenExpiraEm) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiraEm = accessTokenExpiraEm;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @JsonIgnore
    public String getRefreshToken() {
        return refreshToken;
    }

    public OffsetDateTime getAccessTokenExpiraEm() {
        return accessTokenExpiraEm;
    }
}
