package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.app.templatevaga.RequisitoTemplateVagaRepositorio;
import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class RequisitoTemplateVagaJpaAdapter implements RequisitoTemplateVagaRepositorio {
    private final RequisitoTemplateVagaJpaRepository repository;
    private final RequisitoTemplateVagaJpaMapper mapper;
    RequisitoTemplateVagaJpaAdapter(RequisitoTemplateVagaJpaRepository repository, RequisitoTemplateVagaJpaMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public Optional<RequisitoTemplateVaga> buscarAtivoPorId(UUID id) { if (id == null) return Optional.empty(); return repository.findByIdAndExcluidoEmIsNull(id).map(mapper::paraDominio); }
    public List<RequisitoTemplateVaga> listarAtivosDoTemplate(UUID templateId) { return repository.findByTemplateVagaIdAndExcluidoEmIsNull(templateId).stream().map(mapper::paraDominio).collect(Collectors.toList()); }
    public RequisitoTemplateVaga salvar(RequisitoTemplateVaga requisito) { return mapper.paraDominio(repository.save(mapper.paraEntidade(requisito))); }
}
