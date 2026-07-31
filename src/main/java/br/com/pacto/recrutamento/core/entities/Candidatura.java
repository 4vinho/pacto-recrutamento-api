package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Candidatura {
    private UUID id;
    private UUID candidatoId;
    private UUID vagaId;
    private StatusCandidatura status = StatusCandidatura.ENVIADA;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;
    private OffsetDateTime canceladoEm;

    public Candidatura() {
        id = UUID.randomUUID();
        criadoEm = OffsetDateTime.now();
        atualizadoEm = criadoEm;
    }
    public Candidatura(UUID candidatoId, UUID vagaId) {
        this();
        this.candidatoId = candidatoId;
        this.vagaId = vagaId;
    }

    public void cancelar(OffsetDateTime data) {
        if (data == null) {
            throw new IllegalArgumentException("A data de cancelamento é obrigatória");
        }
        setStatus(StatusCandidatura.CANCELADA);
        canceladoEm = data;
    }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCandidatoId() { return candidatoId; }
    public void setCandidatoId(UUID candidatoId) { this.candidatoId = candidatoId; }
    public UUID getVagaId() { return vagaId; }
    public void setVagaId(UUID vagaId) { this.vagaId = vagaId; }
    public StatusCandidatura getStatus() { return status; }
    public void setStatus(StatusCandidatura novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O status da candidatura é obrigatório");
        }
        if (novoStatus == status) {
            return;
        }
        if (!permiteTransicaoPara(novoStatus)) {
            throw new IllegalStateException(
                    "Transição de candidatura inválida: " + status + " -> " + novoStatus);
        }
        status = novoStatus;
        if (novoStatus == StatusCandidatura.CANCELADA && canceladoEm == null) {
            canceladoEm = OffsetDateTime.now();
        }
    }

    private boolean permiteTransicaoPara(StatusCandidatura novoStatus) {
        if (status == StatusCandidatura.ENVIADA) {
            return novoStatus == StatusCandidatura.EM_ANALISE
                    || novoStatus == StatusCandidatura.CANCELADA;
        }
        if (status == StatusCandidatura.EM_ANALISE) {
            return novoStatus == StatusCandidatura.APROVADA
                    || novoStatus == StatusCandidatura.REJEITADA
                    || novoStatus == StatusCandidatura.CANCELADA;
        }
        return false;
    }
    public OffsetDateTime getCanceladoEm() { return canceladoEm; }
    public void setCanceladoEm(OffsetDateTime canceladoEm) { this.canceladoEm = canceladoEm; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
