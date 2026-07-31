package br.com.pacto.recrutamento.infra.adapters.vaga;

import br.com.pacto.recrutamento.infra.repositorys.vaga.VagaJpaRepository;

import br.com.pacto.recrutamento.app.ports.templatevaga.VagaTemplateAdapter;
import br.com.pacto.recrutamento.app.ports.vaga.VagaAdapter;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class VagaJpaAdapter implements VagaAdapter, VagaTemplateAdapter {
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
