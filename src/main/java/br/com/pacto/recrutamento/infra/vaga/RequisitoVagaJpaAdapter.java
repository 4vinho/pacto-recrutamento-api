package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.vaga.RequisitoVagaRepositorio;
import br.com.pacto.recrutamento.app.ports.templatevaga.RequisitoVagaTemplateRepositorio;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequisitoVagaJpaAdapter implements RequisitoVagaRepositorio, RequisitoVagaTemplateRepositorio {
    private final RequisitoVagaJpaRepository repository;
    private final RequisitoVagaJpaMapper mapper;

    public RequisitoVagaJpaAdapter(RequisitoVagaJpaRepository repository, RequisitoVagaJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Optional<RequisitoVaga> buscarAtivoPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id).map(mapper::paraDominio);
    }

    public RequisitoVaga salvar(RequisitoVaga requisito) {
        return mapper.paraDominio(repository.save(mapper.paraEntidade(requisito)));
    }
}
