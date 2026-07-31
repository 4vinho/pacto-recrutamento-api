package br.com.pacto.recrutamento.infra.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NotificacaoJpaRepository extends JpaRepository<NotificacaoJpaEntity, UUID> {
    Optional<NotificacaoJpaEntity> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
}
