package br.com.pacto.recrutamento.app.ports.out.notificacao.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.UUID;

public class DestinatariosCandidatura {
    private final Collection<UUID> responsaveisIds;
    private final UUID usuarioId;
    private final String tituloVaga;
    private final String nomeCandidato;

    public DestinatariosCandidatura(Collection<UUID> responsaveisIds, UUID usuarioId) {
        this(responsaveisIds, usuarioId, null, null);
    }

    public DestinatariosCandidatura(Collection<UUID> responsaveisIds, UUID usuarioId,
                                    String tituloVaga, String nomeCandidato) {
        this.responsaveisIds = Collections.unmodifiableSet(new LinkedHashSet<>(responsaveisIds));
        this.usuarioId = usuarioId;
        this.tituloVaga = tituloVaga;
        this.nomeCandidato = nomeCandidato;
    }

    public Collection<UUID> getResponsaveisIds() {
        return responsaveisIds;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getTituloVaga() {
        return tituloVaga;
    }

    public String getNomeCandidato() {
        return nomeCandidato;
    }
}
