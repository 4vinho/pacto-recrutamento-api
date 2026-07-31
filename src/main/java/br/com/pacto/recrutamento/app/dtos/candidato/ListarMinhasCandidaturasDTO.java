package br.com.pacto.recrutamento.app.dtos.candidato;

import java.util.UUID;

public class ListarMinhasCandidaturasDTO {
    private final UUID usuarioId;
    private final int page;
    private final int pageSize;

    public ListarMinhasCandidaturasDTO(UUID usuarioId, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.page = page;
        this.pageSize = pageSize;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }
}
