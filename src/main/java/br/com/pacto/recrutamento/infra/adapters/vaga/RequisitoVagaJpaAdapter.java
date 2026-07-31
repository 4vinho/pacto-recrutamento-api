package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.RequisitoVagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.vaga.RequisitoVagaPort;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.infra.repositorys.vaga.RequisitoVagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequisitoVagaJpaAdapter implements RequisitoVagaPort, RequisitoVagaTemplatePort {
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
