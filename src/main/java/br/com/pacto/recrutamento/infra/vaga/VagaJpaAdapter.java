package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.vaga.VagaRepositorio;
import br.com.pacto.recrutamento.app.ports.templatevaga.VagaTemplateRepositorio;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VagaJpaAdapter implements VagaRepositorio, VagaTemplateRepositorio {
    private final VagaJpaRepository repository;
    private final VagaJpaMapper mapper;

    public VagaJpaAdapter(VagaJpaRepository repository, VagaJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Optional<Vaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id).map(mapper::paraDominio);
    }

    public Vaga salvar(Vaga vaga) {
        return mapper.paraDominio(repository.save(mapper.paraEntidade(vaga)));
    }
}
