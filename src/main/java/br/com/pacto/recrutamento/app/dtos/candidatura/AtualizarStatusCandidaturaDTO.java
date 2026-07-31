package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.util.UUID;

public class AtualizarStatusCandidaturaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID candidaturaId;
    private final StatusCandidatura status;

    public AtualizarStatusCandidaturaDTO(UUID usuarioSolicitanteId, UUID candidaturaId,
                                         StatusCandidatura status) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.candidaturaId = candidaturaId;
        this.status = status;
    }

    public UUID getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public UUID getCandidaturaId() { return candidaturaId; }
    public StatusCandidatura getStatus() { return status; }
}
