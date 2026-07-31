package br.com.pacto.recrutamento.infra.adapters.notificacao;

import br.com.pacto.recrutamento.infra.repositorys.notificacao.NotificacaoJpaRepository;

import br.com.pacto.recrutamento.app.ports.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
}
