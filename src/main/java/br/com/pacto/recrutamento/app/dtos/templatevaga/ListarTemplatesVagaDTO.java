package br.com.pacto.recrutamento.app.dtos.templatevaga;

import java.util.UUID;

public class ListarTemplatesVagaDTO {
    private final UUID usuarioId;
    private final String busca;
    private final int page;
    private final int pageSize;
    public ListarTemplatesVagaDTO(UUID usuarioId, String busca, int page, int pageSize) {
        this.usuarioId = usuarioId;
        this.busca = busca;
        this.page = page;
        this.pageSize = pageSize;
    }
    public UUID getUsuarioId() { return usuarioId; }
    public String getBusca() { return busca; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
}
