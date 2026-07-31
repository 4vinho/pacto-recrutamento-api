package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.stereotype.Component;

@Component
public class PerguntaVagaJpaMapper {
    PerguntaVagaJpaEntity paraEntidade(PerguntaVaga pergunta) {
        return new PerguntaVagaJpaEntity(pergunta.getId(), pergunta.getVagaId(),
                pergunta.getEnunciado(), pergunta.getTipoResposta(), pergunta.isObrigatoria(),
                pergunta.getOrdem(), pergunta.getCriadoEm(), pergunta.getAtualizadoEm(),
                pergunta.getExcluidoEm());
    }

    PerguntaVaga paraDominio(PerguntaVagaJpaEntity entity) {
        PerguntaVaga pergunta = new PerguntaVaga();
        pergunta.setId(entity.getId());
        pergunta.setVagaId(entity.getVagaId());
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
