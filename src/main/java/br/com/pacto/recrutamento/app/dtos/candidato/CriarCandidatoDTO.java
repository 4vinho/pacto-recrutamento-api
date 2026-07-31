package br.com.pacto.recrutamento.app.dtos.candidato;

import java.time.LocalDate;
import java.util.UUID;

public class CriarCandidatoDTO {
    private final UUID usuarioId;
    private final LocalDate dataAdmissao;

    public CriarCandidatoDTO(UUID usuarioId, LocalDate dataAdmissao) {
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }
}
