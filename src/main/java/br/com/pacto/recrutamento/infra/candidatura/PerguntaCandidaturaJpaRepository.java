package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.infra.vaga.PerguntaVagaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

interface PerguntaCandidaturaJpaRepository extends JpaRepository<PerguntaVagaJpaEntity, UUID> {
    Optional<PerguntaCandidaturaProjection> findProjectedByIdAndExcluidoEmIsNull(UUID id);
}
