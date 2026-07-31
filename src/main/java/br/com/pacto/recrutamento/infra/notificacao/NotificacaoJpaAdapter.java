package br.com.pacto.recrutamento.infra.notificacao;

import br.com.pacto.recrutamento.app.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class NotificacaoJpaAdapter implements NotificacaoPort {
    private final NotificacaoJpaRepository repository;
    private final NotificacaoJpaMapper mapper = new NotificacaoJpaMapper();
    public NotificacaoJpaAdapter(NotificacaoJpaRepository repository) { this.repository = repository; }
    public Optional<Notificacao> buscarPorEventoEDestinatario(UUID eventoId, UUID destinatarioId) {
        return repository.findByEventoIdAndUsuarioId(eventoId, destinatarioId).map(mapper::paraDominio);
    }
    public Notificacao salvar(Notificacao notificacao) {
        NotificacaoJpaEntity entidade = repository.findById(notificacao.getId())
                .orElseGet(() -> mapper.paraJpa(notificacao));
        atualizarEstado(entidade, notificacao);
        return mapper.paraDominio(repository.save(entidade));
    }

    private void atualizarEstado(NotificacaoJpaEntity entidade, Notificacao notificacao) {
        entidade.setStatus(notificacao.getStatus());
        entidade.setTentativas(notificacao.getTentativas());
        entidade.setUltimoErro(notificacao.getUltimoErro());
    }
}
