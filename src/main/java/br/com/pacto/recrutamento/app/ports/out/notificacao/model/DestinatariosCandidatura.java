package br.com.pacto.recrutamento.app.ports.out.notificacao.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;

public class DestinatariosCandidatura {
    private final Collection<UUID> responsaveisIds;
    private final UUID usuarioId;

    public DestinatariosCandidatura(Collection<UUID> responsaveisIds, UUID usuarioId) {
        this.responsaveisIds = Collections.unmodifiableSet(new LinkedHashSet<>(responsaveisIds));
        this.usuarioId = usuarioId;
    }

    public Collection<UUID> getResponsaveisIds() {
        return responsaveisIds;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }
}
