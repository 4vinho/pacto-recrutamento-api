package br.com.pacto.recrutamento.core.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RefreshToken extends Entidade {
    private UUID usuarioId;
    private String tokenHash;
    private UUID familiaId;
    private OffsetDateTime expiraEm;
    private OffsetDateTime usadoEm;
    private OffsetDateTime revogadoEm;
    private OffsetDateTime criadoEm;

    public RefreshToken() {}
    public RefreshToken(UUID usuarioId, String tokenHash, UUID familiaId, OffsetDateTime expiraEm) {
        super(UUID.randomUUID());
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.familiaId = familiaId;
        this.expiraEm = expiraEm;
        this.criadoEm = OffsetDateTime.now();
    }

    public boolean podeCriarSessao(OffsetDateTime agora) {
        return usadoEm == null && revogadoEm == null && expiraEm.isAfter(agora);
    }
    public void marcarComoUsado(OffsetDateTime data) { usadoEm = data; }
    public void revogar(OffsetDateTime data) { revogadoEm = data; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public UUID getFamiliaId() { return familiaId; }
    public void setFamiliaId(UUID familiaId) { this.familiaId = familiaId; }
    public OffsetDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(OffsetDateTime expiraEm) { this.expiraEm = expiraEm; }
    public OffsetDateTime getUsadoEm() { return usadoEm; }
    public void setUsadoEm(OffsetDateTime usadoEm) { this.usadoEm = usadoEm; }
    public OffsetDateTime getRevogadoEm() { return revogadoEm; }
    public void setRevogadoEm(OffsetDateTime revogadoEm) { this.revogadoEm = revogadoEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
