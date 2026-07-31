package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.enums.TipoResposta;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "perguntas_vaga")
public class PerguntaVagaJpaEntity {
    @Id private UUID id;
    @Column(name = "vaga_id", nullable = false) private UUID vagaId;
    @Column(nullable = false, columnDefinition = "TEXT") private String enunciado;
    @Enumerated(EnumType.STRING) @Column(name = "tipo_resposta", nullable = false, length = 30) private TipoResposta tipoResposta;
    @Column(nullable = false) private boolean obrigatoria;
    @Column(nullable = false) private int ordem;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;
    @Column(name = "excluido_em") private OffsetDateTime excluidoEm;

    protected PerguntaVagaJpaEntity() {}
    PerguntaVagaJpaEntity(UUID id, UUID vagaId, String enunciado, TipoResposta tipoResposta,
                          boolean obrigatoria, int ordem, OffsetDateTime criadoEm,
                          OffsetDateTime atualizadoEm, OffsetDateTime excluidoEm) {
        this.id = id; this.vagaId = vagaId; this.enunciado = enunciado; this.tipoResposta = tipoResposta;
        this.obrigatoria = obrigatoria; this.ordem = ordem; this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm; this.excluidoEm = excluidoEm;
    }
    @PrePersist void incluir() { OffsetDateTime agora = OffsetDateTime.now(); if (criadoEm == null) criadoEm = agora; atualizadoEm = agora; }
    @PreUpdate void atualizar() { atualizadoEm = OffsetDateTime.now(); }
    UUID getId() { return id; }
    UUID getVagaId() { return vagaId; }
    String getEnunciado() { return enunciado; }
    TipoResposta getTipoResposta() { return tipoResposta; }
    boolean isObrigatoria() { return obrigatoria; }
    int getOrdem() { return ordem; }
    OffsetDateTime getCriadoEm() { return criadoEm; }
    OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    OffsetDateTime getExcluidoEm() { return excluidoEm; }
}
