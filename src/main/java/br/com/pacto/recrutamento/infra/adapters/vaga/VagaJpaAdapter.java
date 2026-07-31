package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.app.ports.out.templatevaga.VagaTemplatePort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.infra.repositorys.vaga.VagaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VagaJpaAdapter implements VagaPort, VagaTemplatePort {
    private final VagaJpaRepository repository;

    public VagaJpaAdapter(VagaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Vaga> buscarAtivaPorId(UUID id) {
        if (id == null) return Optional.empty();
        return repository.findByIdAndExcluidoEmIsNull(id);
    }

    public Vaga salvar(Vaga vaga) {
        return repository.save(vaga);
    }
}
