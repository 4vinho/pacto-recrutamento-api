package br.com.pacto.recrutamento.infra.adapters.templatevaga;

import br.com.pacto.recrutamento.infra.repositorys.templatevaga.RequisitoTemplateVagaJpaRepository;

import br.com.pacto.recrutamento.app.ports.templatevaga.RequisitoTemplateVagaAdapter;
import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class RequisitoTemplateVagaJpaAdapter implements RequisitoTemplateVagaAdapter {
    private final RequisitoTemplateVagaJpaRepository repository;

    RequisitoTemplateVagaJpaAdapter(RequisitoTemplateVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<RequisitoTemplateVaga> buscarAtivoPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public List<RequisitoTemplateVaga> listarAtivosDoTemplate(UUID templateId) {
        return repository.findByTemplateVagaIdAndExcluidoEmIsNull(templateId);
    }

    public RequisitoTemplateVaga salvar(RequisitoTemplateVaga requisito) {
        return repository.save(requisito);
    }
}
