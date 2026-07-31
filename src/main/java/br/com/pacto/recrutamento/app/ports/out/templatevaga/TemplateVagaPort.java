package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.TemplateVaga;

import java.util.Optional;
import java.util.UUID;

public interface TemplateVagaPort {
    Optional<TemplateVaga> buscarAtivoPorId(UUID id);

    TemplateVaga salvar(TemplateVaga template);
}
