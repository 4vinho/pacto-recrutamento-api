package br.com.pacto.recrutamento.app.ports.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;

import java.util.Optional;
import java.util.UUID;

public interface TemplateVagaAdapter {
    Optional<TemplateVaga> buscarAtivoPorId(UUID id);

    TemplateVaga salvar(TemplateVaga template);
}
