package br.com.pacto.recrutamento.infra.repositorys.vaga;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface PerguntaVagaJpaRepository extends JpaRepository<PerguntaVaga, UUID> {
    List<PerguntaVaga> findAllByVagaIdAndExcluidoEmIsNullOrderByOrdemAsc(UUID vagaId);
    Optional<PerguntaVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
