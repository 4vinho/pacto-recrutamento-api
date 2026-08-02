package br.com.pacto.recrutamento.app.ports.out.vaga;

import java.util.UUID;

public interface AutorizacaoVagaPort {
    boolean podeManterVagas(UUID usuarioId);
    boolean podeExcluirVagas(UUID usuarioId);
}
