package br.com.pacto.recrutamento.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "curriculos", uniqueConstraints = @UniqueConstraint(name = "uk_curriculos_storage_key", columnNames = "storage_key"))
public class Curriculo extends EntidadeAuditavel {
    @Column(name = "candidato_id", nullable = false)
    private UUID candidatoId;
    @Column(name = "storage_key", nullable = false, length = 500)
    private String storageKey;
    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;
    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;
    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;
    @Column(name = "checksum_sha256", nullable = false, columnDefinition = "CHAR(64)")
    private String checksumSha256;
    @Column(name = "excluido_em")
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
