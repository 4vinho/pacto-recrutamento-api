package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CandidaturaDTO {
    private final UUID id;
    private final UUID candidatoId;
    private final UUID vagaId;
    private final StatusCandidatura status;
    private final OffsetDateTime criadaEm;

    public CandidaturaDTO(UUID id, UUID candidatoId, UUID vagaId,
                          StatusCandidatura status, OffsetDateTime criadaEm) {
        this.id = id;
        this.candidatoId = candidatoId;
        this.vagaId = vagaId;
        this.status = status;
        this.criadaEm = criadaEm;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCandidatoId() {
        return candidatoId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public StatusCandidatura getStatus() {
        return status;
    }

    public OffsetDateTime getCriadaEm() {
        return criadaEm;
    }
}
