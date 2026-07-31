package br.com.pacto.recrutamento.app.dtos.candidato;

import java.time.LocalDate;
import java.util.UUID;

public class AtualizarCandidatoDTO {
    private final UUID usuarioId;
    private final LocalDate dataAdmissao;

    public AtualizarCandidatoDTO(UUID usuarioId, LocalDate dataAdmissao) {
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
