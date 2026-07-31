package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.time.OffsetDateTime;

public class UrlTemporariaCurriculoDTO {
    private final String url;
    private final OffsetDateTime expiraEm;

    public UrlTemporariaCurriculoDTO(String url, OffsetDateTime expiraEm) {
        this.url = url;
        this.expiraEm = expiraEm;
    }

    public String getUrl() {
        return url;
    }

    public OffsetDateTime getExpiraEm() {
        return expiraEm;
    }
}
