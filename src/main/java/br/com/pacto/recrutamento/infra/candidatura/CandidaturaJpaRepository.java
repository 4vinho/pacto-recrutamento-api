package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface CandidaturaJpaRepository extends JpaRepository<Candidatura, UUID> {
    boolean existsByCandidatoIdAndVagaId(UUID candidatoId, UUID vagaId);
}
