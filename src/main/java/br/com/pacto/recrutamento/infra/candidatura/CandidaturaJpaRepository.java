package br.com.pacto.recrutamento.infra.candidatura;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface CandidaturaJpaRepository extends JpaRepository<CandidaturaJpaEntity, UUID> {
    boolean existsByCandidatoIdAndVagaId(UUID candidatoId, UUID vagaId);
    Optional<CandidaturaProjection> findProjectedById(UUID id);
}
