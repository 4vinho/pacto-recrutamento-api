package br.com.pacto.recrutamento.app.candidato;

import java.time.LocalDate;
import java.util.UUID;

public class CandidatoPersistido {
    private final UUID id;
    private final UUID usuarioId;
    private final LocalDate dataAdmissao;

    public CandidatoPersistido(UUID id, UUID usuarioId, LocalDate dataAdmissao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
}
