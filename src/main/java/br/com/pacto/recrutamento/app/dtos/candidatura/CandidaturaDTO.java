package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CandidaturaDTO {
    private final UUID id;
    private final UUID usuarioId;
    private final UUID vagaId;
    private final String tituloVaga;
    private final StatusCandidatura status;
    private final OffsetDateTime criadaEm;
    private final boolean perguntasRespondidas;
    private final boolean requisitosRespondidos;

    public CandidaturaDTO(UUID id, UUID usuarioId, UUID vagaId,
                          StatusCandidatura status, OffsetDateTime criadaEm) {
        this(id, usuarioId, vagaId, null, status, criadaEm, false, false);
    }

    public CandidaturaDTO(UUID id, UUID usuarioId, UUID vagaId, String tituloVaga,
                          StatusCandidatura status, OffsetDateTime criadaEm,
                          boolean perguntasRespondidas, boolean requisitosRespondidos) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.tituloVaga = tituloVaga;
        this.status = status;
        this.criadaEm = criadaEm;
        this.perguntasRespondidas = perguntasRespondidas;
        this.requisitosRespondidos = requisitosRespondidos;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getVagaId() {
        return vagaId;
    }
    public String getTituloVaga() { return tituloVaga; }

    public StatusCandidatura getStatus() {
        return status;
    }

    public OffsetDateTime getCriadaEm() {
        return criadaEm;
    }

    public boolean isPerguntasRespondidas() { return perguntasRespondidas; }

    public boolean isRequisitosRespondidos() { return requisitosRespondidos; }
}
