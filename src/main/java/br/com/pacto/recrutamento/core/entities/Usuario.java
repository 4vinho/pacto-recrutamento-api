package br.com.pacto.recrutamento.core.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email"))
public class Usuario extends EntidadeAuditavel {
    @Column(name = "email", nullable = false, length = 254)
    private String email;
    @Column(name = "telefone", length = 20)
    private String telefone;
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
    @ManyToMany
    @JoinTable(name = "usuarios_papeis",
            joinColumns = @JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "fk_usuarios_papeis_usuario")),
            inverseJoinColumns = @JoinColumn(name = "papel_id", foreignKey = @ForeignKey(name = "fk_usuarios_papeis_papel")),
            uniqueConstraints = @UniqueConstraint(name = "pk_usuarios_papeis", columnNames = {"usuario_id", "papel_id"}))
    private Set<Papel> papeis = new HashSet<>();
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;
    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao = LocalDate.now();

    public Usuario() {
    }

    public Usuario(String email, String senhaHash) {
        super(UUID.randomUUID());
        this.email = normalizarEmail(email);
        this.senhaHash = senhaHash;
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizarEmail(email);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Set<Papel> getPapeis() {
        return papeis;
    }

    public void setPapeis(Set<Papel> papeis) {
        this.papeis = new HashSet<>(papeis);
    }

    public OffsetDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(OffsetDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
    public LocalDate getDataAdmissao() { return dataAdmissao; }
    public void setDataAdmissao(LocalDate dataAdmissao) { this.dataAdmissao = dataAdmissao; }
}
