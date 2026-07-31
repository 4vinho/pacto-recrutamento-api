package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.templatevaga.RequisitoVagaTemplateRepositorio;
import br.com.pacto.recrutamento.app.ports.vaga.RequisitoVagaRepositorio;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequisitoVagaJpaAdapter implements RequisitoVagaRepositorio, RequisitoVagaTemplateRepositorio {
    private final RequisitoVagaJpaRepository repository;

    public RequisitoVagaJpaAdapter(RequisitoVagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<RequisitoVaga> buscarAtivoPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public RequisitoVaga salvar(RequisitoVaga requisito) {
        return repository.save(requisito);
    }
}
