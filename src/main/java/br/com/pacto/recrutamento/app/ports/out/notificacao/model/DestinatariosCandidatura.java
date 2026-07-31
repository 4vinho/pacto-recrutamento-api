package br.com.pacto.recrutamento.app.ports.out.notificacao.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;

public class DestinatariosCandidatura {
    private final Collection<UUID> responsaveisIds;
    private final UUID candidatoId;

    public DestinatariosCandidatura(Collection<UUID> responsaveisIds, UUID candidatoId) {
        this.responsaveisIds = Collections.unmodifiableSet(new LinkedHashSet<>(responsaveisIds));
        this.candidatoId = candidatoId;
    }

    public Collection<UUID> getResponsaveisIds() {
        return responsaveisIds;
    }

    public UUID getCandidatoId() {
        return candidatoId;
    }
}
