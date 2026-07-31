package br.com.pacto.recrutamento.infra.notificacao;

import java.util.UUID;

public interface DestinatariosCandidaturaProjection {
    UUID getResponsavelId();

    UUID getCandidatoId();
}
