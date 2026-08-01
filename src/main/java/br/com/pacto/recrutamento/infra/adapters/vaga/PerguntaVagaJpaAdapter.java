package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.PerguntaVagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.vaga.PerguntaVagaPort;
import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.infra.repositorys.vaga.PerguntaVagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public class PerguntaVagaJpaAdapter implements PerguntaVagaPort, PerguntaVagaTemplatePort {
    private final PerguntaVagaJpaRepository repository;

    public PerguntaVagaJpaAdapter(PerguntaVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<PerguntaVaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public List<PerguntaVaga> listarAtivasPorVagaId(UUID vagaId) {
        return repository.findAllByVagaIdAndExcluidoEmIsNullOrderByOrdemAsc(vagaId);
    }

    public PerguntaVaga salvar(PerguntaVaga pergunta) {
        return repository.save(pergunta);
    }
}
