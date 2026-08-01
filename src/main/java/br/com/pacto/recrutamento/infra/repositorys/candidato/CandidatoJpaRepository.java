package br.com.pacto.recrutamento.infra.repositorys.candidato;

import br.com.pacto.recrutamento.infra.projections.CandidaturaPainelProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CandidatoJpaRepository extends JpaRepository<br.com.pacto.recrutamento.core.entities.Candidato, UUID> {
    boolean existsByUsuarioId(UUID usuarioId);

    Optional<br.com.pacto.recrutamento.core.entities.Candidato> findByUsuarioId(UUID usuarioId);

    @Query(value = "SELECT CAST(c.id AS VARCHAR) AS candidaturaId, "
            + "CAST(v.id AS VARCHAR) AS vagaId, "
            + "v.titulo AS tituloVaga, c.status AS status, c.criado_em AS criadaEm, "
            + "CAST(NULL AS VARCHAR) AS feedback "
            + "FROM candidaturas c "
            + "JOIN candidatos ca ON ca.id = c.candidato_id "
            + "JOIN vagas v ON v.id = c.vaga_id "
            + "WHERE ca.usuario_id = :usuarioId "
            + "ORDER BY c.criado_em DESC",
            countQuery = "SELECT COUNT(*) FROM candidaturas c "
                    + "JOIN candidatos ca ON ca.id = c.candidato_id "
                    + "WHERE ca.usuario_id = :usuarioId",
            nativeQuery = true)
    Page<CandidaturaPainelProjection> listarPainel(
            @Param("usuarioId") UUID usuarioId, Pageable pageable);
}
