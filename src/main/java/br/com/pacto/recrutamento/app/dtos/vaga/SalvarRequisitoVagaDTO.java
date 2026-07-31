package br.com.pacto.recrutamento.app.dtos.vaga;

import java.util.UUID;

public class SalvarRequisitoVagaDTO {
    private final UUID usuarioSolicitanteId;
    private final UUID vagaId;
    private final UUID requisitoId;
    private final String descricao;
    private final boolean obrigatorio;

    public SalvarRequisitoVagaDTO(UUID usuarioSolicitanteId, UUID vagaId,
                                  UUID requisitoId, String descricao, boolean obrigatorio) {
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.vagaId = vagaId;
        this.requisitoId = requisitoId;
        this.descricao = descricao;
        this.obrigatorio = obrigatorio;
    }

    public UUID getUsuarioSolicitanteId() {
        return usuarioSolicitanteId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public UUID getRequisitoId() {
        return requisitoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isObrigatorio() {
        return obrigatorio;
    }
}
