package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "vagas")
public class Vaga {
    @Id
    private UUID id;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vagas_responsaveis", joinColumns = @JoinColumn(name = "vaga_id"))
    @Column(name = "usuario_id", nullable = false)
    private Set<UUID> responsaveisIds = new LinkedHashSet<>();
    @Column(nullable = false, length = 150)
    private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusVaga status = StatusVaga.RASCUNHO;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public Vaga() {
        id = UUID.randomUUID();
    }

    public Vaga(Collection<UUID> responsaveisIds, String titulo, String descricao) {
        this();
        setResponsaveisIds(responsaveisIds);
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public Vaga(UUID responsavelId, String titulo, String descricao) {
        this(Collections.singleton(responsavelId), titulo, descricao);
    }

    public static Vaga restaurar(UUID id, UUID responsavelId, String titulo, String descricao,
                                 StatusVaga status, OffsetDateTime criadoEm,
                                 OffsetDateTime atualizadoEm, OffsetDateTime excluidoEm) {
        Vaga vaga = new Vaga(responsavelId, titulo, descricao);
        vaga.id = id;
        vaga.status = status;
        vaga.criadoEm = criadoEm;
        vaga.atualizadoEm = atualizadoEm;
        vaga.excluidoEm = excluidoEm;
        return vaga;
    }

    public boolean aceitaCandidatura() {
        return status == StatusVaga.PUBLICADA && excluidoEm == null;
    }

    @PrePersist
    void incluir() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (criadoEm == null) criadoEm = agora;
        atualizadoEm = agora;
    }

    @PreUpdate
    void atualizar() {
        atualizadoEm = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<UUID> getResponsaveisIds() {
        return Collections.unmodifiableSet(responsaveisIds);
    }

    public void setResponsaveisIds(Collection<UUID> responsaveisIds) {
        if (responsaveisIds == null || responsaveisIds.isEmpty() || responsaveisIds.contains(null)) {
            throw new IllegalArgumentException("A vaga deve possuir ao menos um responsavel");
        }
        this.responsaveisIds.clear();
        this.responsaveisIds.addAll(responsaveisIds);
    }

    public boolean possuiResponsavel(UUID usuarioId) {
        return usuarioId != null && responsaveisIds.contains(usuarioId);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga novoStatus) {
        if (novoStatus == null) throw new IllegalArgumentException("O status da vaga e obrigatorio");
        if (novoStatus == status) return;
        if (!permiteTransicaoPara(novoStatus)) {
            throw new IllegalStateException("Transicao de vaga invalida: " + status + " -> " + novoStatus);
        }
        status = novoStatus;
    }

    private boolean permiteTransicaoPara(StatusVaga novoStatus) {
        if (status == StatusVaga.RASCUNHO) {
            return novoStatus == StatusVaga.PUBLICADA || novoStatus == StatusVaga.CANCELADA;
        }
        if (status == StatusVaga.PUBLICADA) {
            return novoStatus == StatusVaga.ENCERRADA || novoStatus == StatusVaga.CANCELADA;
        }
        return false;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(OffsetDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public OffsetDateTime getExcluidoEm() {
        return excluidoEm;
    }

    public void setExcluidoEm(OffsetDateTime excluidoEm) {
        this.excluidoEm = excluidoEm;
    }
}
