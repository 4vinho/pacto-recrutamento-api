package br.com.pacto.recrutamento.app.ports.notificacao;

import br.com.pacto.recrutamento.app.serviceImpl.NotificacaoServiceImpl;

import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.infra.notificacao.NotificacaoJpaAdapter;
import br.com.pacto.recrutamento.infra.notificacao.NotificacaoJpaEntity;
import org.junit.jupiter.api.Test;
import javax.persistence.Entity;
import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoArquiteturaTest {
    @Test
    void dominioNaoTemJpaEAdaptadorPermaneceNaInfraestrutura() {
        assertThat(Notificacao.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(NotificacaoServiceImpl.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.serviceImpl");
        assertThat(NotificacaoJpaEntity.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.infra.notificacao");
        assertThat(NotificacaoJpaAdapter.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.infra.notificacao");
    }
}
