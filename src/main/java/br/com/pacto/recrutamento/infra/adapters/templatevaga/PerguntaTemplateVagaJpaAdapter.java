package br.com.pacto.recrutamento.infra.adapters.templatevaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.PerguntaTemplateVagaPort;
import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import br.com.pacto.recrutamento.infra.repositorys.templatevaga.PerguntaTemplateVagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class PerguntaTemplateVagaJpaAdapter implements PerguntaTemplateVagaPort {
    private final PerguntaTemplateVagaJpaRepository repository;

    PerguntaTemplateVagaJpaAdapter(PerguntaTemplateVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<PerguntaTemplateVaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public List<PerguntaTemplateVaga> listarAtivasDoTemplate(UUID templateId) {
        return repository.findByTemplateVagaIdAndExcluidoEmIsNull(templateId);
    }

    public PerguntaTemplateVaga salvar(PerguntaTemplateVaga pergunta) {
        return repository.save(pergunta);
    }
}
