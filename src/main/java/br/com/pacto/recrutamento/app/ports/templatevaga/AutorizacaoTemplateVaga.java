package br.com.pacto.recrutamento.app.ports.templatevaga;

import java.util.UUID;

public interface AutorizacaoTemplateVaga {
    boolean podeManterTemplates(UUID usuarioId);
}
