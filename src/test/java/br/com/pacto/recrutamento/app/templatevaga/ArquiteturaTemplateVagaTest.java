package br.com.pacto.recrutamento.app.ports.templatevaga;

import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;

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
    void aplicacaoECoreNaoDependemDeJpaSpringOuInfra() throws Exception {
        assertThat(Class.forName("br.com.pacto.recrutamento.app.serviceImpl.TemplateVagaServiceImpl")).isNotNull();
        assertThat(Class.forName("br.com.pacto.recrutamento.app.ports.templatevaga.TemplateVagaRepositorio")).isNotNull();
        assertThat(TemplateVaga.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(PerguntaTemplateVaga.class.isAnnotationPresent(Entity.class)).isFalse();
        assertThat(RequisitoTemplateVaga.class.isAnnotationPresent(Entity.class)).isFalse();
        for (Class<?> type : new Class<?>[] { TemplateVagaServiceImpl.class, TemplateVagaRepositorio.class,
                PerguntaTemplateVagaRepositorio.class, RequisitoTemplateVagaRepositorio.class }) {
            for (Class<?> dependency : type.getInterfaces()) assertThat(dependency.getName()).doesNotContain("infra");
            assertThat(type.getPackage().getName()).doesNotContain("infra");
        }
    }

    @Test
    void copiaETransacionalNaBordaDeInfraestrutura() throws Exception {
        Class<?> facade = Class.forName("br.com.pacto.recrutamento.infra.templatevaga.TemplateVagaConfiguration$Transacional");
        Method metodo = facade.getMethod("criarVagaAPartirDoTemplate",
                br.com.pacto.recrutamento.app.dtos.templatevaga.CriarVagaAPartirDoTemplateDTO.class);
        assertThat(metodo.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class)).isTrue();
    }
}
