package br.com.pacto.recrutamento.infra.notificacao;

import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface NotificacaoJpaRepository extends JpaRepository<Notificacao, UUID> {
    Optional<Notificacao> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);
}
