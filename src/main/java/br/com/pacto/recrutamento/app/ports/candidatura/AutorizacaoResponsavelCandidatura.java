package br.com.pacto.recrutamento.app.ports.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.UUID;

public interface AutorizacaoResponsavelCandidatura {
    boolean podeGerenciar(UUID usuarioId, Vaga vaga);
}
