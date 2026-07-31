package br.com.pacto.recrutamento.infra.projections.notificacao;

import java.util.UUID;

public interface DestinatariosCandidaturaProjection {
    UUID getResponsavelId();

    UUID getCandidatoId();
}
