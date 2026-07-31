package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.core.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken t set t.revogadoEm = :data where t.familiaId = :familiaId and t.revogadoEm is null")
    void revogarFamilia(@Param("familiaId") UUID familiaId, @Param("data") OffsetDateTime data);

    @Modifying
    @Query("update RefreshToken t set t.revogadoEm = :data where t.usuarioId = :usuarioId and t.revogadoEm is null")
    void revogarTodosDoUsuario(@Param("usuarioId") UUID usuarioId, @Param("data") OffsetDateTime data);
}
