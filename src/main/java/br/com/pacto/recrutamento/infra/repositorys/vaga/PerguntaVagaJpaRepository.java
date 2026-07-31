package br.com.pacto.recrutamento.infra.repositorys.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerguntaVagaJpaRepository extends JpaRepository<PerguntaVaga, UUID> {
    Optional<PerguntaVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
