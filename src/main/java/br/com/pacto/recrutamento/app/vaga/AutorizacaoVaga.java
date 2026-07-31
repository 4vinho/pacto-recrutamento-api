package br.com.pacto.recrutamento.app.vaga;

import java.util.UUID;

public interface AutorizacaoVaga {
    boolean podeManterVagas(UUID usuarioId);
}
