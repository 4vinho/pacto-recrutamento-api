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
    private final String busca;
    private final UUID requisitoId;
    private final Boolean atendeTodosRequisitos;

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, int page, int pageSize) {
        this(usuarioId, vagaId, status, null, null, null, null, null, page, pageSize);
    }

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, String busca, UUID requisitoId,
            NivelAtendimentoRequisito nivelMinimo, Integer tempoEmpresaMeses,
            Boolean atendeTodosRequisitos, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.status = status;
        this.page = page;
        this.pageSize = pageSize;
        this.nivelMinimo = nivelMinimo;
        this.tempoEmpresaMeses = tempoEmpresaMeses;
        this.busca = busca;
        this.requisitoId = requisitoId;
        this.atendeTodosRequisitos = atendeTodosRequisitos;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public NivelAtendimentoRequisito getNivelMinimo() { return nivelMinimo; }
    public Integer getTempoEmpresaMeses() { return tempoEmpresaMeses; }
    public String getBusca() { return busca; }
    public UUID getRequisitoId() { return requisitoId; }
    public Boolean getAtendeTodosRequisitos() { return atendeTodosRequisitos; }
}
