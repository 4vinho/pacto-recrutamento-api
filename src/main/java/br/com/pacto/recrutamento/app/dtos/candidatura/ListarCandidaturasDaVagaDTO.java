package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.util.UUID;

public class ListarCandidaturasDaVagaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;
    private final StatusCandidatura status;
    private final int page;
    private final int pageSize;

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.status = status;
        this.page = page;
        this.pageSize = pageSize;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
}
