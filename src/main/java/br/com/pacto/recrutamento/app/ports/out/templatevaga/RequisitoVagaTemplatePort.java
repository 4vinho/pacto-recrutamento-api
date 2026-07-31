package br.com.pacto.recrutamento.app.ports.out.templatevaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;

public interface RequisitoVagaTemplatePort {
    RequisitoVaga salvar(RequisitoVaga requisito);
}
