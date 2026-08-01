package br.com.pacto.recrutamento.infra.repositorys.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface VagaJpaRepository extends JpaRepository<Vaga, UUID>, JpaSpecificationExecutor<Vaga> {
    Optional<Vaga> findByIdAndExcluidoEmIsNull(UUID id);
}
