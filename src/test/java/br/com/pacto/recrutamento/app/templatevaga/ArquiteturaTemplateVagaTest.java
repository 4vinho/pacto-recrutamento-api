package br.com.pacto.recrutamento.app.ports.templatevaga;

import br.com.pacto.recrutamento.app.serviceImpl.TemplateVagaServiceImpl;
import br.com.pacto.recrutamento.core.entities.PerguntaTemplateVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoTemplateVaga;
import br.com.pacto.recrutamento.core.entities.TemplateVaga;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaTemplateVagaTest {
    @Test
    void aplicacaoNaoDependeDeInfraEEntidadesDoCoreSaoJpa() throws Exception {
        assertThat(Class.forName("br.com.pacto.recrutamento.app.serviceImpl.TemplateVagaServiceImpl")).isNotNull();
        assertThat(Class.forName("br.com.pacto.recrutamento.app.ports.templatevaga.TemplateVagaAdapter")).isNotNull();
        assertThat(TemplateVaga.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(PerguntaTemplateVaga.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(RequisitoTemplateVaga.class.isAnnotationPresent(Entity.class)).isTrue();
        for (Class<?> type : new Class<?>[]{TemplateVagaServiceImpl.class, TemplateVagaAdapter.class,
                PerguntaTemplateVagaAdapter.class, RequisitoTemplateVagaAdapter.class}) {
            for (Class<?> dependency : type.getInterfaces()) assertThat(dependency.getName()).doesNotContain("infra");
            assertThat(type.getPackage().getName()).doesNotContain("infra");
        }
    }

    @Test
    void copiaETransacionalNoServiceDaAplicacao() throws Exception {
        Method metodo = TemplateVagaServiceImpl.class.getMethod("criarVagaAPartirDoTemplate",
                br.com.pacto.recrutamento.app.dtos.templatevaga.CriarVagaAPartirDoTemplateDTO.class);
        assertThat(metodo.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class)).isTrue();
    }
}
