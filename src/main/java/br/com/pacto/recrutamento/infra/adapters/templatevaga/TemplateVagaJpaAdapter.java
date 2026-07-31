package br.com.pacto.recrutamento.infra.adapters.templatevaga;

import br.com.pacto.recrutamento.infra.repositorys.templatevaga.TemplateVagaJpaRepository;

import br.com.pacto.recrutamento.app.ports.templatevaga.TemplateVagaRepositorio;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class TemplateVagaJpaAdapter implements TemplateVagaRepositorio {
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
