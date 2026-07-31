package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface VagaJpaRepository extends JpaRepository<Vaga, UUID> {
    Optional<Vaga> findByIdAndExcluidoEmIsNull(UUID id);
}
