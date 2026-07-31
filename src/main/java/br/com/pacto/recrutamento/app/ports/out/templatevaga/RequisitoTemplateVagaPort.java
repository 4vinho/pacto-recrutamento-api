package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequisitoTemplateVagaPort {
    Optional<RequisitoTemplateVaga> buscarAtivoPorId(UUID id);

    List<RequisitoTemplateVaga> listarAtivosDoTemplate(UUID templateId);

    RequisitoTemplateVaga salvar(RequisitoTemplateVaga requisito);
}
