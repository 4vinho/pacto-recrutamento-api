package br.com.pacto.recrutamento.app.dtos.vaga;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.util.UUID;

public class ListarVagasDTO {
    private final UUID usuarioId;
    private final String busca;
    private final StatusVaga status;
    private final int page;
    private final int pageSize;
    private final String ordenarPor;
    private final boolean ascendente;

    public ListarVagasDTO(UUID usuarioId, String busca, StatusVaga status, int page,
                          int pageSize, String ordenarPor, boolean ascendente) {
        this.usuarioId = usuarioId;
        this.busca = busca;
        this.status = status;
        this.page = page;
        this.pageSize = pageSize;
        this.ordenarPor = ordenarPor;
        this.ascendente = ascendente;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public String getBusca() { return busca; }
    public StatusVaga getStatus() { return status; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public String getOrdenarPor() { return ordenarPor; }
    public boolean isAscendente() { return ascendente; }
}
