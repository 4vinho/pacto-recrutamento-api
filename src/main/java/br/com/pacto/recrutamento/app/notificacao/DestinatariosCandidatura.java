package br.com.pacto.recrutamento.app.notificacao;

import java.util.UUID;

public class DestinatariosCandidatura {
    private final UUID responsavelId;
    private final UUID candidatoId;
    public DestinatariosCandidatura(UUID responsavelId, UUID candidatoId) { this.responsavelId = responsavelId; this.candidatoId = candidatoId; }
    public UUID getResponsavelId() { return responsavelId; }
    public UUID getCandidatoId() { return candidatoId; }
}
