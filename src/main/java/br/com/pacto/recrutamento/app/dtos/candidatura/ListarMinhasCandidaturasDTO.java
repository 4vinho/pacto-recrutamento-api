package br.com.pacto.recrutamento.app.dtos.candidatura;

import java.util.UUID;
import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.time.OffsetDateTime;

public class ListarMinhasCandidaturasDTO {
    private final UUID usuarioId;
    private final int page;
    private final int pageSize;
    private final StatusCandidatura status;
    private final OffsetDateTime inicio;
    private final OffsetDateTime fim;

    public ListarMinhasCandidaturasDTO(UUID usuarioId, int page, int pageSize) {
        this(usuarioId, page, pageSize, null, null, null);
    }

    public ListarMinhasCandidaturasDTO(UUID usuarioId, int page, int pageSize,
            StatusCandidatura status, OffsetDateTime inicio, OffsetDateTime fim) {
        this.usuarioId = usuarioId;
        this.page = page;
        this.pageSize = pageSize;
        this.status = status;
        this.inicio = inicio;
        this.fim = fim;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public StatusCandidatura getStatus() { return status; }
    public OffsetDateTime getInicio() { return inicio; }
    public OffsetDateTime getFim() { return fim; }
}
