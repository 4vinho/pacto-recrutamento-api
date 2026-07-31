package br.com.pacto.recrutamento.infra.repositorys.usuario;

import br.com.pacto.recrutamento.core.entities.TokenRecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TokenRecuperacaoSenhaJpaRepository extends JpaRepository<TokenRecuperacaoSenha, UUID> {
    Optional<TokenRecuperacaoSenha> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update TokenRecuperacaoSenha t set t.usadoEm = :agora where t.id = :id and t.usadoEm is null and t.expiraEm > :agora")
    int consumirSeValido(@Param("id") UUID id, @Param("agora") OffsetDateTime agora);
}
