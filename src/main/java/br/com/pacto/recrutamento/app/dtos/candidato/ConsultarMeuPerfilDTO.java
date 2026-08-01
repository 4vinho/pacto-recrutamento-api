package br.com.pacto.recrutamento.app.dtos.candidato;

import java.util.UUID;

public class ConsultarMeuPerfilDTO {
    private final UUID usuarioId;
    public ConsultarMeuPerfilDTO(UUID usuarioId) { this.usuarioId = usuarioId; }
    public UUID getUsuarioId() { return usuarioId; }
}
