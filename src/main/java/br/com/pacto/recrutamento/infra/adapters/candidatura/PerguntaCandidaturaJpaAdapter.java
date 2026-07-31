package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.infra.repositorys.candidatura.PerguntaCandidaturaJpaRepository;

import br.com.pacto.recrutamento.infra.projections.PerguntaCandidaturaProjection;

import br.com.pacto.recrutamento.app.ports.out.candidatura.PerguntaCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class PerguntaCandidaturaJpaAdapter implements PerguntaCandidaturaPort {
    private final PerguntaCandidaturaJpaRepository repository;

    PerguntaCandidaturaJpaAdapter(PerguntaCandidaturaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
        return repository.findProjectedByIdAndExcluidoEmIsNull(id).map(this::mapear);
    }

    private PerguntaVaga mapear(PerguntaCandidaturaProjection e) {
        PerguntaVaga pergunta = new PerguntaVaga();
        pergunta.setId(e.getId());
        pergunta.setVagaId(e.getVagaId());
        pergunta.setEnunciado(e.getEnunciado());
        pergunta.setTipoResposta(e.getTipoResposta());
        pergunta.setObrigatoria(e.isObrigatoria());
        pergunta.setOrdem(e.getOrdem());
        return pergunta;
    }
}
