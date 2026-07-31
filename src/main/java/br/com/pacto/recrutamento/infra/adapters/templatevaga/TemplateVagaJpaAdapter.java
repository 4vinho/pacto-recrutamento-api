package br.com.pacto.recrutamento.infra.adapters.templatevaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.TemplateVagaPort;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import br.com.pacto.recrutamento.infra.repositorys.templatevaga.TemplateVagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class TemplateVagaJpaAdapter implements TemplateVagaPort {
    private final TemplateVagaJpaRepository repository;

    TemplateVagaJpaAdapter(TemplateVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<TemplateVaga> buscarAtivoPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public TemplateVaga salvar(TemplateVaga template) {
        return repository.save(template);
    }
}
