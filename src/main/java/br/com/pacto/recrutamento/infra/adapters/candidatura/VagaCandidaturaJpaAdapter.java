package br.com.pacto.recrutamento.infra.adapters.candidatura;

import br.com.pacto.recrutamento.infra.repositorys.candidatura.VagaCandidaturaJpaRepository;

import br.com.pacto.recrutamento.app.ports.out.candidatura.VagaCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class VagaCandidaturaJpaAdapter implements VagaCandidaturaPort {
    private final VagaCandidaturaJpaRepository repository;

    VagaCandidaturaJpaAdapter(VagaCandidaturaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Vaga> buscarPorId(UUID id) {
        return repository.findById(id);
    }
}
