package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface RequisitoVagaJpaRepository extends JpaRepository<RequisitoVaga, UUID> {
    Optional<RequisitoVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
