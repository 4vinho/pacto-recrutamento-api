package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusVaga;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Vaga {
    private UUID id;
    private UUID responsavelId;
    private String titulo;
    private String descricao;
    private StatusVaga status = StatusVaga.RASCUNHO;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime excluidoEm;

    public Vaga() {
        id = UUID.randomUUID();
    }

    public Vaga(UUID responsavelId, String titulo, String descricao) {
        this();
        this.responsavelId = responsavelId;
        this.titulo = titulo;
        this.descricao = descricao;
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

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResponsavelId() { return responsavelId; }
    public void setResponsavelId(UUID responsavelId) { this.responsavelId = responsavelId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public StatusVaga getStatus() { return status; }
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

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public OffsetDateTime getExcluidoEm() { return excluidoEm; }
    public void setExcluidoEm(OffsetDateTime excluidoEm) { this.excluidoEm = excluidoEm; }
}
