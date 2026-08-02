package br.com.pacto.recrutamento.app.usecases.templatevaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.PerguntaTemplateVagaPort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.PerguntaVagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.RequisitoTemplateVagaPort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.RequisitoVagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.VagaTemplatePort;
import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;

final class CopiadorTemplateVaga {
    private final VagaTemplatePort vagas;
    private final PerguntaTemplateVagaPort perguntas;
    private final RequisitoTemplateVagaPort requisitos;
    private final PerguntaVagaTemplatePort perguntasVaga;
    private final RequisitoVagaTemplatePort requisitosVaga;

    CopiadorTemplateVaga(VagaTemplatePort vagas, PerguntaTemplateVagaPort perguntas,
                         RequisitoTemplateVagaPort requisitos,
                         PerguntaVagaTemplatePort perguntasVaga,
                         RequisitoVagaTemplatePort requisitosVaga) {
        this.vagas = vagas;
        this.perguntas = perguntas;
        this.requisitos = requisitos;
        this.perguntasVaga = perguntasVaga;
        this.requisitosVaga = requisitosVaga;
    }

    Vaga copiar(TemplateVaga template) {
        Vaga vaga = vagas.salvar(new Vaga(template.getResponsavelId(), template.getTitulo(),
                template.getDescricao()));
        for (PerguntaTemplateVaga origem : perguntas.listarAtivasDoTemplate(template.getId())) {
            PerguntaVaga destino = new PerguntaVaga();
            destino.setVagaId(vaga.getId());
            destino.setEnunciado(origem.getEnunciado());
            destino.setTipoResposta(origem.getTipoResposta());
            destino.setObrigatoria(origem.isObrigatoria());
            destino.setOrdem(origem.getOrdem());
            perguntasVaga.salvar(destino);
        }
        for (RequisitoTemplateVaga origem : requisitos.listarAtivosDoTemplate(template.getId())) {
            RequisitoVaga destino = new RequisitoVaga();
            destino.setVagaId(vaga.getId());
            destino.setDescricao(origem.getDescricao());
            destino.setObrigatorio(origem.isObrigatorio());
            requisitosVaga.salvar(destino);
        }
        return vaga;
    }
}
