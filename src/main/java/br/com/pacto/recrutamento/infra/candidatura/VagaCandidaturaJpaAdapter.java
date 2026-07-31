package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.app.ports.candidatura.VagaCandidaturaRepositorio;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class VagaCandidaturaJpaAdapter implements VagaCandidaturaRepositorio {
    private final VagaCandidaturaJpaRepository repository;

    VagaCandidaturaJpaAdapter(VagaCandidaturaJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Vaga> buscarPorId(UUID id) {
        return repository.findById(id);
    }
}
