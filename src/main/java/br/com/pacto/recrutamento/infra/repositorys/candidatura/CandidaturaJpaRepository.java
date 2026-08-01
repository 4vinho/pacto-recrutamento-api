package br.com.pacto.recrutamento.infra.repositorys.candidatura;

import br.com.pacto.recrutamento.core.entities.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

import java.util.UUID;

public interface CandidaturaJpaRepository extends JpaRepository<Candidatura, UUID>, JpaSpecificationExecutor<Candidatura> {
    boolean existsByUsuarioIdAndVagaId(UUID usuarioId, UUID vagaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Candidatura c where c.id = :id")
    java.util.Optional<Candidatura> findByIdForUpdate(@Param("id") UUID id);
}
