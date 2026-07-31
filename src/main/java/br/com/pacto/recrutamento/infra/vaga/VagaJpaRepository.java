package br.com.pacto.recrutamento.infra.vaga;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface VagaJpaRepository extends JpaRepository<VagaJpaEntity, UUID> {
    Optional<VagaJpaEntity> findByIdAndExcluidoEmIsNull(UUID id);
}
