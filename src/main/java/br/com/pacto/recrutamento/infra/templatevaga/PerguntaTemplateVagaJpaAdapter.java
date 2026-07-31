package br.com.pacto.recrutamento.infra.templatevaga;

import br.com.pacto.recrutamento.app.templatevaga.PerguntaTemplateVagaRepositorio;
import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class PerguntaTemplateVagaJpaAdapter implements PerguntaTemplateVagaRepositorio {
    private final PerguntaTemplateVagaJpaRepository repository;
    private final PerguntaTemplateVagaJpaMapper mapper;
    PerguntaTemplateVagaJpaAdapter(PerguntaTemplateVagaJpaRepository repository, PerguntaTemplateVagaJpaMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public Optional<PerguntaTemplateVaga> buscarAtivaPorId(UUID id) { if (id == null) return Optional.empty(); return repository.findByIdAndExcluidoEmIsNull(id).map(mapper::paraDominio); }
    public List<PerguntaTemplateVaga> listarAtivasDoTemplate(UUID templateId) { return repository.findByTemplateVagaIdAndExcluidoEmIsNull(templateId).stream().map(mapper::paraDominio).collect(Collectors.toList()); }
    public PerguntaTemplateVaga salvar(PerguntaTemplateVaga pergunta) { return mapper.paraDominio(repository.save(mapper.paraEntidade(pergunta))); }
}
