package br.com.pacto.recrutamento.core.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_recuperacao_senha", uniqueConstraints = @UniqueConstraint(name = "uk_tokens_recuperacao_senha_token_hash", columnNames = "token_hash"))
public class TokenRecuperacaoSenha extends Entidade {
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Column(name = "token_hash", nullable = false, length = 64) private String tokenHash;
    @Column(name = "expira_em", nullable = false) private OffsetDateTime expiraEm;
    @Column(name = "usado_em") private OffsetDateTime usadoEm;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    public TokenRecuperacaoSenha() { }
    public TokenRecuperacaoSenha(UUID usuarioId, String tokenHash, OffsetDateTime expiraEm) {
        super(UUID.randomUUID()); this.usuarioId = usuarioId; this.tokenHash = tokenHash; this.expiraEm = expiraEm; this.criadoEm = OffsetDateTime.now();
    }
    @PrePersist private void inicializarCriacao() { if (criadoEm == null) criadoEm = OffsetDateTime.now(); }
    public boolean podeSerConsumido(OffsetDateTime agora) { return usadoEm == null && expiraEm.isAfter(agora); }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public OffsetDateTime getExpiraEm() { return expiraEm; }
    public OffsetDateTime getUsadoEm() { return usadoEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
}
