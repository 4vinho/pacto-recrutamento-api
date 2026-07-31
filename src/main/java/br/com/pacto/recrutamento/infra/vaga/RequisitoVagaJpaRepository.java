package br.com.pacto.recrutamento.infra.vaga;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface RequisitoVagaJpaRepository extends JpaRepository<RequisitoVagaJpaEntity, UUID> {
    Optional<RequisitoVagaJpaEntity> findByIdAndExcluidoEmIsNull(UUID id);
}
