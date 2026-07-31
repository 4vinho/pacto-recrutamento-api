package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vagas")
public class Vaga extends EntidadeAuditavel {
    @Column(name = "responsavel_id", nullable = false)
    private UUID responsavelId;
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusVaga status = StatusVaga.RASCUNHO;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    public Vaga() {}
    public Vaga(UUID responsavelId, String titulo, String descricao) {
        super(UUID.randomUUID());
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public boolean aceitaCandidatura() {
        return status == StatusVaga.PUBLICADA && excluidoEm == null;
    }
    public UUID getResponsavelId() { return responsavelId; }
    public void setResponsavelId(UUID responsavelId) { this.responsavelId = responsavelId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusVaga getStatus() { return status; }
    public void setStatus(StatusVaga novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O status da vaga é obrigatório");
        }
        if (novoStatus == status) {
            return;
        }
        if (!permiteTransicaoPara(novoStatus)) {
            throw new IllegalStateException(
                    "Transição de vaga inválida: " + status + " -> " + novoStatus);
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
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
