package br.com.pacto.recrutamento.app.dtos.candidato;

import br.com.pacto.recrutamento.app.dtos.curriculo.CurriculoDTO;

import java.time.LocalDate;
import java.util.UUID;

public class CandidatoDTO {
    private final UUID id;
    private final UUID usuarioId;
    private final LocalDate dataAdmissao;
    private final CurriculoDTO curriculo;

    public CandidatoDTO(UUID id, UUID usuarioId, LocalDate dataAdmissao) {
        this(id, usuarioId, dataAdmissao, null);
    }

    public CandidatoDTO(UUID id, UUID usuarioId, LocalDate dataAdmissao, CurriculoDTO curriculo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.dataAdmissao = dataAdmissao;
        this.curriculo = curriculo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public CurriculoDTO getCurriculo() { return curriculo; }
}
