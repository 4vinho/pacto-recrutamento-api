package br.com.pacto.recrutamento.infra.projections;

import java.util.UUID;

public interface DestinatariosCandidaturaProjection {
    UUID getResponsavelId();

    UUID getCandidatoId();
}
