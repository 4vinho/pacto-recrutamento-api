package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.NomePapel;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Usuario extends EntidadeAuditavel {
    private String email;
    private String telefone;
    private String senhaHash;
    private boolean ativo = true;
    private Set<NomePapel> papeis = new HashSet<>();
    private OffsetDateTime excluidoEm;

    public Usuario() {}
    public Usuario(String email, String senhaHash) {
        super(UUID.randomUUID());
        this.email = normalizarEmail(email);
        this.senhaHash = senhaHash;
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = normalizarEmail(email); }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public Set<NomePapel> getPapeis() { return papeis; }
    public void setPapeis(Set<NomePapel> papeis) { this.papeis = new HashSet<>(papeis); }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
