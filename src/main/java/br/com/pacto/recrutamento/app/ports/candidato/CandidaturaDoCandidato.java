package br.com.pacto.recrutamento.app.ports.candidato;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CandidaturaDoCandidato {
    private final UUID candidaturaId;
    private final UUID vagaId;
    private final String tituloVaga;
    private final StatusCandidatura status;
    private final OffsetDateTime criadaEm;
    private final String feedback;

    public CandidaturaDoCandidato(UUID candidaturaId, UUID vagaId, String tituloVaga,
                                  StatusCandidatura status, OffsetDateTime criadaEm, String feedback) {
        this.candidaturaId = candidaturaId;
        this.vagaId = vagaId;
        this.tituloVaga = tituloVaga;
        this.status = status;
        this.criadaEm = criadaEm;
        this.feedback = feedback;
    }

    public UUID getCandidaturaId() { return candidaturaId; }
    public UUID getVagaId() { return vagaId; }
    public String getTituloVaga() { return tituloVaga; }
    public StatusCandidatura getStatus() { return status; }
    public OffsetDateTime getCriadaEm() { return criadaEm; }
    public String getFeedback() { return feedback; }
}
