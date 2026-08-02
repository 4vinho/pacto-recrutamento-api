package br.com.pacto.recrutamento.app.dtos.candidatura;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;
import java.util.UUID;
import br.com.pacto.recrutamento.core.enums.NivelAtendimentoRequisito;
import br.com.pacto.recrutamento.core.common.FiltroRespostaCandidatura;
import java.util.List;

public class ListarCandidaturasDaVagaDTO {
    private final UUID usuarioId;
    private final UUID vagaId;
    private final StatusCandidatura status;
    private final int page;
    private final int pageSize;
    private final NivelAtendimentoRequisito nivelMinimo;
    private final String busca;
    private final List<FiltroRespostaCandidatura> filtrosRespostas;
    private final UUID requisitoId;
    private final Boolean atendeTodosRequisitos;

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, int page, int pageSize) {
        this(usuarioId, vagaId, status, null, java.util.Collections.emptyList(), null, null, null, page, pageSize);
    }

    public ListarCandidaturasDaVagaDTO(UUID usuarioId, UUID vagaId,
            StatusCandidatura status, String busca, List<FiltroRespostaCandidatura> filtrosRespostas,
            UUID requisitoId, NivelAtendimentoRequisito nivelMinimo,
            Boolean atendeTodosRequisitos, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.vagaId = vagaId;
        this.status = status;
        this.page = page;
        this.pageSize = pageSize;
        this.nivelMinimo = nivelMinimo;
        this.busca = busca;
        this.filtrosRespostas = filtrosRespostas == null ? java.util.Collections.emptyList() : filtrosRespostas;
        this.requisitoId = requisitoId;
        this.atendeTodosRequisitos = atendeTodosRequisitos;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public UUID getVagaId() { return vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public NivelAtendimentoRequisito getNivelMinimo() { return nivelMinimo; }
    public String getBusca() { return busca; }
    public List<FiltroRespostaCandidatura> getFiltrosRespostas() { return filtrosRespostas; }
    public UUID getRequisitoId() { return requisitoId; }
    public Boolean getAtendeTodosRequisitos() { return atendeTodosRequisitos; }
}
