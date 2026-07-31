package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.app.templatevaga.TemplateVagaRepositorio;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
class TemplateVagaJpaAdapter implements TemplateVagaRepositorio {
    private final TemplateVagaJpaRepository repository;
    private final TemplateVagaJpaMapper mapper;
    TemplateVagaJpaAdapter(TemplateVagaJpaRepository repository, TemplateVagaJpaMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public Optional<TemplateVaga> buscarAtivoPorId(UUID id) { if (id == null) return Optional.empty(); return repository.findByIdAndExcluidoEmIsNull(id).map(mapper::paraDominio); }
    public TemplateVaga salvar(TemplateVaga template) { return mapper.paraDominio(repository.save(mapper.paraEntidade(template))); }
}
