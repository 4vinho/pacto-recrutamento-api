package br.com.pacto.recrutamento.app.ports.templatevaga;

import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerguntaTemplateVagaAdapter {
    Optional<PerguntaTemplateVaga> buscarAtivaPorId(UUID id);

    List<PerguntaTemplateVaga> listarAtivasDoTemplate(UUID templateId);

    PerguntaTemplateVaga salvar(PerguntaTemplateVaga pergunta);
}
