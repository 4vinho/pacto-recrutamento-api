package br.com.pacto.recrutamento.app.dtos.usuario;

import java.util.UUID;

public class EncerrarSessaoDTO {
    private final UUID usuarioId;
    private final String refreshToken;

    public EncerrarSessaoDTO(UUID usuarioId, String refreshToken) {
        this.usuarioId = usuarioId;
        this.refreshToken = refreshToken;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public String getRefreshToken() { return refreshToken; }
}
