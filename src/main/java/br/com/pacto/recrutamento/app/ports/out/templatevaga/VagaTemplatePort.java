package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.Vaga;

public interface VagaTemplatePort {
    Vaga salvar(Vaga vaga);
}
