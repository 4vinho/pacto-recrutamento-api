package br.com.pacto.recrutamento.app.dtos.curriculo;

import java.util.UUID;

public class CurriculoDTO {
    private final UUID id;
    private final String nomeOriginal;
    private final String contentType;
    private final long tamanhoBytes;
    private final String checksumSha256;

    public CurriculoDTO(UUID id, String nomeOriginal, String contentType,
                        long tamanhoBytes, String checksumSha256) {
        this.id = id;
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.tamanhoBytes = tamanhoBytes;
        this.checksumSha256 = checksumSha256;
    }

    public UUID getId() { return id; }
    public String getNomeOriginal() { return nomeOriginal; }
    public String getContentType() { return contentType; }
    public long getTamanhoBytes() { return tamanhoBytes; }
    public String getChecksumSha256() { return checksumSha256; }
}
