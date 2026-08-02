package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import java.util.UUID;

public interface AutorizacaoTemplateVagaPort {
    boolean podeManterTemplates(UUID usuarioId);
}
