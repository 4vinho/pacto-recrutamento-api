package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.app.ports.out.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.infra.repositorys.notificacao.NotificacaoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;
import java.time.OffsetDateTime;
import br.com.pacto.recrutamento.core.enums.StatusNotificacao;

@Repository
public class NotificacaoJpaAdapter implements NotificacaoPort {
    private final NotificacaoJpaRepository repository;

    public NotificacaoJpaAdapter(NotificacaoJpaRepository repository) {
        this.repository = repository;
    }

    public Optional<Notificacao> buscarPorEventoEDestinatario(UUID eventoId, UUID destinatarioId) {
        return repository.findByEventoIdAndUsuarioId(eventoId, destinatarioId);
    }

    public Notificacao salvar(Notificacao notificacao) {
        return repository.save(notificacao);
    }

    @Override
    public List<Notificacao> buscarParaReprocessamento(OffsetDateTime limite, int maximoTentativas) {
        return repository.findTop50ByStatusInAndTentativasLessThanAndAtualizadoEmBeforeOrderByAtualizadoEmAsc(
                Arrays.asList(StatusNotificacao.PENDENTE, StatusNotificacao.FALHA), maximoTentativas, limite);
    }
}
