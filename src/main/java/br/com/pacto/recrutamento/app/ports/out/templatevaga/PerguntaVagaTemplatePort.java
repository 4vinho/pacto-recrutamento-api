package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;

public interface PerguntaVagaTemplatePort {
    PerguntaVaga salvar(PerguntaVaga pergunta);
}
