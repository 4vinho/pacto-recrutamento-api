package br.com.pacto.recrutamento.app.ports.notificacao;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import java.util.Optional;
import java.util.UUID;
public interface NotificacaoPort {
    Optional<Notificacao> buscarPorEventoEDestinatario(UUID eventoId, UUID destinatarioId);
    Notificacao salvar(Notificacao notificacao);
}
