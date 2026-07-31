package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.vaga.PerguntaVagaRepositorio;
import br.com.pacto.recrutamento.app.ports.templatevaga.PerguntaVagaTemplateRepositorio;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PerguntaVagaJpaAdapter implements PerguntaVagaRepositorio, PerguntaVagaTemplateRepositorio {
    private final PerguntaVagaJpaRepository repository;

    public PerguntaVagaJpaAdapter(PerguntaVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public PerguntaVaga salvar(PerguntaVaga pergunta) {
        return repository.save(pergunta);
    }
}
