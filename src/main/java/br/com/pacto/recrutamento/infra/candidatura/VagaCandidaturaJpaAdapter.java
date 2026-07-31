package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.app.candidatura.VagaCandidaturaRepositorio;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.infra.vaga.VagaJpaEntity;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
class VagaCandidaturaJpaAdapter implements VagaCandidaturaRepositorio {
    private final VagaCandidaturaJpaRepository repository;
    VagaCandidaturaJpaAdapter(VagaCandidaturaJpaRepository repository) { this.repository = repository; }
    public Optional<Vaga> buscarPorId(UUID id) { return repository.findById(id).map(this::mapear); }
    private Vaga mapear(VagaJpaEntity e) {
        return Vaga.restaurar(e.getId(), e.getResponsavelId(), e.getTitulo(), e.getDescricao(),
                e.getStatus(), e.getCriadoEm(), e.getAtualizadoEm(), e.getExcluidoEm());
    }
}
