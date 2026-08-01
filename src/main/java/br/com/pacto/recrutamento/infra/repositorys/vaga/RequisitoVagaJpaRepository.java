package br.com.pacto.recrutamento.infra.repositorys.vaga;

import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface RequisitoVagaJpaRepository extends JpaRepository<RequisitoVaga, UUID> {
    List<RequisitoVaga> findAllByVagaIdAndExcluidoEmIsNull(UUID vagaId);
    List<RequisitoVaga> findAllByVagaIdAndExcluidoEmIsNullOrderByCriadoEmAsc(UUID vagaId);
    Optional<RequisitoVaga> findByIdAndExcluidoEmIsNull(UUID id);
}
