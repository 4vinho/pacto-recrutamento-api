package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface PerguntaCandidaturaJpaRepository extends JpaRepository<PerguntaVaga, UUID> {
    Optional<PerguntaCandidaturaProjection> findProjectedByIdAndExcluidoEmIsNull(UUID id);
}
