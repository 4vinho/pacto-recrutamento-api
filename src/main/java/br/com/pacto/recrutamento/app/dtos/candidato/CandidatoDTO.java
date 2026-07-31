package br.com.pacto.recrutamento.app.dtos.candidato;

import java.time.LocalDate;
import java.util.UUID;

public class CandidatoDTO {
    private final UUID id;
    private final UUID usuarioId;
    private final LocalDate dataAdmissao;

    public CandidatoDTO(UUID id, UUID usuarioId, LocalDate dataAdmissao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
}
