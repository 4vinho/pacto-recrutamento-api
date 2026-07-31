package br.com.pacto.recrutamento.app.ports.vaga;

import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;

import br.com.pacto.recrutamento.core.entities.PerguntaVaga;
import br.com.pacto.recrutamento.core.entities.RequisitoVaga;
import br.com.pacto.recrutamento.core.entities.Vaga;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;

class ArquiteturaVagaTest {

    @Test
    void entidadesDoCoreSaoPersistidasDiretamente() throws Exception {
        assertThat(Class.forName("br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl")).isNotNull();
        assertThat(Class.forName("br.com.pacto.recrutamento.app.ports.vaga.VagaRepositorio")).isNotNull();
        assertThat(Vaga.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(PerguntaVaga.class.isAnnotationPresent(Entity.class)).isTrue();
        assertThat(RequisitoVaga.class.isAnnotationPresent(Entity.class)).isTrue();
    }
}
