package br.com.pacto.recrutamento.infra.repositorys.candidatura;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.infra.projections.PerguntaCandidaturaProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaCandidaturaJpaRepository extends JpaRepository<PerguntaVaga, UUID> {
    Optional<PerguntaCandidaturaProjection> findProjectedByIdAndExcluidoEmIsNull(UUID id);
}
