package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface PerguntaVagaJpaRepository extends JpaRepository<PerguntaVaga, UUID> {
    Optional<PerguntaVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
