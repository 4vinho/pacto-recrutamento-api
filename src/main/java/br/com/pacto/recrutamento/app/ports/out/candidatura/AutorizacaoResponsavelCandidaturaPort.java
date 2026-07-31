package br.com.pacto.recrutamento.app.ports.out.candidatura;

import br.com.pacto.recrutamento.core.entities.Vaga;

import java.util.UUID;

public interface AutorizacaoResponsavelCandidaturaPort {
    boolean podeGerenciar(UUID usuarioId, Vaga vaga);
}
