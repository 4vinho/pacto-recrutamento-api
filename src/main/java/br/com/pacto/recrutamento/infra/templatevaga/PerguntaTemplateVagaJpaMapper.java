package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import org.springframework.stereotype.Component;

@Component
class PerguntaTemplateVagaJpaMapper {
    PerguntaTemplateVagaJpaEntity paraEntidade(PerguntaTemplateVaga pergunta) {
        return new PerguntaTemplateVagaJpaEntity(pergunta.getId(), pergunta.getTemplateVagaId(),
                pergunta.getEnunciado(), pergunta.getTipoResposta(), pergunta.isObrigatoria(),
                pergunta.getOrdem(), pergunta.getCriadoEm(), pergunta.getAtualizadoEm(), pergunta.getExcluidoEm());
    }

    PerguntaTemplateVaga paraDominio(PerguntaTemplateVagaJpaEntity entity) {
        PerguntaTemplateVaga pergunta = new PerguntaTemplateVaga();
        pergunta.setId(entity.getId());
        pergunta.setTemplateVagaId(entity.getTemplateVagaId());
        pergunta.setEnunciado(entity.getEnunciado());
        pergunta.setTipoResposta(entity.getTipoResposta());
        pergunta.setObrigatoria(entity.isObrigatoria());
        pergunta.setOrdem(entity.getOrdem());
        pergunta.setCriadoEm(entity.getCriadoEm());
        pergunta.setAtualizadoEm(entity.getAtualizadoEm());
        pergunta.setExcluidoEm(entity.getExcluidoEm());
        return pergunta;
    }
}
