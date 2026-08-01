package br.com.pacto.recrutamento.infra.repositorys.notificacao;

import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.Collection;
import java.util.UUID;
import java.time.OffsetDateTime;
import br.com.pacto.recrutamento.core.enums.StatusNotificacao;

public interface NotificacaoJpaRepository extends JpaRepository<Notificacao, UUID> {
    Optional<Notificacao> findByEventoIdAndUsuarioId(UUID eventoId, UUID usuarioId);

    List<Notificacao> findTop50ByStatusInAndTentativasLessThanAndAtualizadoEmBeforeOrderByAtualizadoEmAsc(
            Collection<StatusNotificacao> statuses, int tentativas, OffsetDateTime atualizadoEm);
}
