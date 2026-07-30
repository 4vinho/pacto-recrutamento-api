package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Curriculo extends EntidadeAuditavel {
    private UUID candidatoId;
    private String storageKey;
    private String nomeOriginal;
    private String contentType;
    private long tamanhoBytes;
    private String checksumSha256;
    private OffsetDateTime excluidoEm;

    public Curriculo() {}
    public Curriculo(UUID candidatoId, String storageKey, String nomeOriginal,
                     String contentType, long tamanhoBytes, String checksumSha256) {
        super(UUID.randomUUID());
        this.candidatoId = candidatoId;
        this.storageKey = storageKey;
        this.nomeOriginal = nomeOriginal;
        this.contentType = contentType;
        this.tamanhoBytes = tamanhoBytes;
        this.checksumSha256 = checksumSha256;
    }

    public UUID getCandidatoId() { return candidatoId; }
    public void setCandidatoId(UUID candidatoId) { this.candidatoId = candidatoId; }
    public String getStorageKey() { return storageKey; }
    public void setStorageKey(String storageKey) { this.storageKey = storageKey; }
    public String getNomeOriginal() { return nomeOriginal; }
    public void setNomeOriginal(String nomeOriginal) { this.nomeOriginal = nomeOriginal; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getTamanhoBytes() { return tamanhoBytes; }
    public void setTamanhoBytes(long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    public String getChecksumSha256() { return checksumSha256; }
    public void setChecksumSha256(String checksumSha256) { this.checksumSha256 = checksumSha256; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
