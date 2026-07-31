package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.StatusCandidatura;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidaturas", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidaturas_candidato_vaga", columnNames = {"candidato_id", "vaga_id"}))
public class Candidatura {
    @Id
    private UUID id;
    @Column(name = "candidato_id", nullable = false)
    private UUID candidatoId;
    @Column(name = "vaga_id", nullable = false)
    private UUID vagaId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusCandidatura status = StatusCandidatura.RASCUNHO;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;
    @Column(name = "cancelado_em")
    private OffsetDateTime canceladoEm;
    @Column(name = "perguntas_respondidas", nullable = false)
    private boolean perguntasRespondidas;
    @Column(name = "requisitos_respondidos", nullable = false)
    private boolean requisitosRespondidos;

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

    @PrePersist
    void incluir() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (criadoEm == null) {
            criadoEm = agora;
        }
        atualizadoEm = agora;
    }

    @PreUpdate
    void atualizar() {
        atualizadoEm = OffsetDateTime.now();
    }

    public void cancelar(OffsetDateTime data) {
        if (data == null) {
            throw new IllegalArgumentException("A data de cancelamento é obrigatória");
        }
        setStatus(StatusCandidatura.CANCELADA);
        canceladoEm = data;
    }

    public void configurarEtapas(boolean possuiPerguntas, boolean possuiRequisitos) {
        perguntasRespondidas = !possuiPerguntas;
        requisitosRespondidos = !possuiRequisitos;
        enviarSeCompleta();
    }

    public void registrarPerguntasRespondidas() {
        if (perguntasRespondidas) throw new IllegalStateException("Perguntas ja respondidas");
        perguntasRespondidas = true;
        enviarSeCompleta();
    }

    public void registrarRequisitosRespondidos() {
        if (requisitosRespondidos) throw new IllegalStateException("Requisitos ja respondidos");
        requisitosRespondidos = true;
        enviarSeCompleta();
    }

    private void enviarSeCompleta() {
        if (status == StatusCandidatura.RASCUNHO && perguntasRespondidas && requisitosRespondidos) {
            setStatus(StatusCandidatura.ENVIADA);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCandidatoId() {
        return candidatoId;
    }

    public void setCandidatoId(UUID candidatoId) {
        this.candidatoId = candidatoId;
    }

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public StatusCandidatura getStatus() {
        return status;
    }

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
        if (status == StatusCandidatura.RASCUNHO) {
            return novoStatus == StatusCandidatura.ENVIADA
                    || novoStatus == StatusCandidatura.CANCELADA;
        }
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

    public OffsetDateTime getCanceladoEm() {
        return canceladoEm;
    }

    public void setCanceladoEm(OffsetDateTime canceladoEm) {
        this.canceladoEm = canceladoEm;
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

    public boolean isPerguntasRespondidas() { return perguntasRespondidas; }

    public boolean isRequisitosRespondidos() { return requisitosRespondidos; }

    public void setPerguntasRespondidas(boolean perguntasRespondidas) {
        this.perguntasRespondidas = perguntasRespondidas;
    }

    public void setRequisitosRespondidos(boolean requisitosRespondidos) {
        this.requisitosRespondidos = requisitosRespondidos;
    }
}
