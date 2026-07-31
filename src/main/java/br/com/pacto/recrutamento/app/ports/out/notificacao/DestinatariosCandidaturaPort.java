package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.model.DestinatariosCandidatura;

import java.util.Optional;
import java.util.UUID;

public interface DestinatariosCandidaturaPort {
    Optional<DestinatariosCandidatura> buscarPorCandidatura(UUID candidaturaId);
}
