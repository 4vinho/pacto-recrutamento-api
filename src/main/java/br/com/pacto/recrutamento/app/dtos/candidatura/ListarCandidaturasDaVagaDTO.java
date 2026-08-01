package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.util.UUID;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;

public class ListarCandidaturasDaVagaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;
    private final StatusCandidatura status;
    private final int page;
    private final int pageSize;
    private final NivelAtendimentoRequisito nivelMinimo;
    private final Integer tempoEmpresaMeses;

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, int page, int pageSize) {
        this(usuarioId, vagaId, status, null, null, page, pageSize);
    }

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, NivelAtendimentoRequisito nivelMinimo,
            Integer tempoEmpresaMeses, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.status = status;
        this.page = page;
        this.pageSize = pageSize;
        this.nivelMinimo = nivelMinimo;
        this.tempoEmpresaMeses = tempoEmpresaMeses;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public NivelAtendimentoRequisito getNivelMinimo() { return nivelMinimo; }
    public Integer getTempoEmpresaMeses() { return tempoEmpresaMeses; }
}
