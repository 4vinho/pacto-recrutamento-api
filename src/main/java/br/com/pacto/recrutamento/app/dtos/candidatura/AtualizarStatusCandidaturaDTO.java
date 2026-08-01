package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.util.UUID;

public class AtualizarStatusCandidaturaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID candidaturaId;
    private final StatusCandidatura status;
    private final String feedback;
    private final Long versao;

    public AtualizarStatusCandidaturaDTO(UUID usuarioSolicitanteId, UUID candidaturaId,
                                         StatusCandidatura status) {
        this(usuarioSolicitanteId, candidaturaId, status, null, null);
    }

    public AtualizarStatusCandidaturaDTO(UUID usuarioSolicitanteId, UUID candidaturaId,
            StatusCandidatura status, String feedback, Long versao) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.candidaturaId = candidaturaId;
        this.status = status;
        this.feedback = feedback;
        this.versao = versao;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getCandidaturaId() {
        return candidaturaId;
    }

    public StatusCandidatura getStatus() {
        return status;
    }
    public String getFeedback() { return feedback; }
    public Long getVersao() { return versao; }
}
