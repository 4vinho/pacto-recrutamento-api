package br.com.pacto.recrutamento.app.ports.out.notificacao;

import br.com.pacto.recrutamento.app.usecases.notificacao.NotificacaoService;
import br.com.pacto.recrutamento.core.entities.Notificacao;
import br.com.pacto.recrutamento.infra.adapters.notificacao.NotificacaoJpaAdapter;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacaoArquiteturaTest {
    @Test
    void notificacaoDoCoreEhEntidadeJpaEAdaptadorPermaneceNaInfraestrutura() {
        assertThat(Notificacao.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(NotificacaoService.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.usecases.notificacao");
        assertThat(NotificacaoJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.adapters.notificacao");
    }
}
