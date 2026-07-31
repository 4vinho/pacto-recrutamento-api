package br.com.pacto.recrutamento.app.ports.vaga;

import java.util.UUID;

public interface AutorizacaoVaga {
    boolean podeManterVagas(UUID usuarioId);
}
