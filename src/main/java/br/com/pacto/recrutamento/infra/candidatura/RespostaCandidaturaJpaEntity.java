package br.com.pacto.recrutamento.infra.candidatura;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "respostas_candidatura", uniqueConstraints = @UniqueConstraint(
        name = "uk_respostas_candidatura_pergunta", columnNames = {"candidatura_id", "pergunta_id"}))
public class RespostaCandidaturaJpaEntity {
    @Id private UUID id;
    @Column(name = "candidatura_id", nullable = false) private UUID candidaturaId;
    @Column(name = "pergunta_id", nullable = false) private UUID perguntaId;
    @Column(nullable = false, columnDefinition = "TEXT") private String valor;
    @Column(name = "criado_em", nullable = false, updatable = false) private OffsetDateTime criadoEm;
    @Column(name = "atualizado_em", nullable = false) private OffsetDateTime atualizadoEm;

    protected RespostaCandidaturaJpaEntity() {}
    RespostaCandidaturaJpaEntity(UUID id, UUID candidaturaId, UUID perguntaId, String valor,
                                  OffsetDateTime criadoEm, OffsetDateTime atualizadoEm) {
        this.id = id; this.candidaturaId = candidaturaId; this.perguntaId = perguntaId;
        this.valor = valor; this.criadoEm = criadoEm; this.atualizadoEm = atualizadoEm;
    }
    @PrePersist void incluir() { OffsetDateTime agora = OffsetDateTime.now(); if (criadoEm == null) criadoEm = agora; atualizadoEm = agora; }
}
