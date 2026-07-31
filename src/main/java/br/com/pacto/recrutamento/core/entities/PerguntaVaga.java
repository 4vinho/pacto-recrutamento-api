package br.com.pacto.recrutamento.core.entities;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perguntas_vaga")
public class PerguntaVaga {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "vaga_id", nullable = false)
    private UUID vagaId;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resposta", nullable = false, length = 30)
    private TipoResposta tipoResposta;
    @Column(nullable = false)
    private boolean obrigatoria;
    @Column(nullable = false)
    private int ordem;
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

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

    public UUID getVagaId() {
        return vagaId;
    }

    public void setVagaId(UUID vagaId) {
        this.vagaId = vagaId;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public TipoResposta getTipoResposta() {
        return tipoResposta;
    }

    public void setTipoResposta(TipoResposta tipoResposta) {
        this.tipoResposta = tipoResposta;
    }

    public boolean isObrigatoria() {
        return obrigatoria;
    }

    public void setObrigatoria(boolean obrigatoria) {
        this.obrigatoria = obrigatoria;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        if (ordem <= 0) throw new IllegalArgumentException("A ordem da pergunta deve ser positiva");
        this.ordem = ordem;
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
