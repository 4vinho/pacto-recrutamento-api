package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.core.entities.Notificacao;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

public interface NotificacaoPort {
    Optional<Notificacao> buscarPorEventoEDestinatario(UUID eventoId, UUID destinatarioId);

    Notificacao salvar(Notificacao notificacao);

    List<Notificacao> buscarParaReprocessamento(OffsetDateTime limite, int maximoTentativas);
}
