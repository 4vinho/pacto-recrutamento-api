package br.com.pacto.recrutamento.app.ports.notificacao;

import br.com.pacto.recrutamento.app.serviceImpl.NotificacaoServiceImpl;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.infra.notificacao.NotificacaoJpaAdapter;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoArquiteturaTest {
    @Test
    void notificacaoDoCoreEhEntidadeJpaEAdaptadorPermaneceNaInfraestrutura() {
        assertThat(Notificacao.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(NotificacaoServiceImpl.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.serviceImpl");
        assertThat(NotificacaoJpaAdapter.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.infra.notificacao");
    }
}
