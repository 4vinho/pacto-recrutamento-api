package br.com.pacto.recrutamento.infra.notificacao;

import br.com.pacto.recrutamento.core.entities.Notificacao;
import java.time.OffsetDateTime;

public class NotificacaoJpaMapper {
    public NotificacaoJpaEntity paraJpa(Notificacao notificacao) {
        NotificacaoJpaEntity entidade = new NotificacaoJpaEntity();
        entidade.setId(notificacao.getId()); entidade.setEventoId(notificacao.getEventoId()); entidade.setUsuarioId(notificacao.getUsuarioId());
        entidade.setTipo(notificacao.getTipo()); entidade.setTitulo(notificacao.getTitulo()); entidade.setMensagem(notificacao.getMensagem());
        entidade.setStatus(notificacao.getStatus()); entidade.setTentativas(notificacao.getTentativas()); entidade.setUltimoErro(notificacao.getUltimoErro());
        return entidade;
    }
    public Notificacao paraDominio(NotificacaoJpaEntity entidade) {
        return new Notificacao(entidade.getId(), entidade.getEventoId(), entidade.getUsuarioId(), entidade.getTipo(), entidade.getTitulo(),
                entidade.getMensagem(), entidade.getStatus(), entidade.getTentativas(), entidade.getUltimoErro());
    }
}
