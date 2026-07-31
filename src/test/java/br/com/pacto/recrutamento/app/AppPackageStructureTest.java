package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoAdapter;
import br.com.pacto.recrutamento.app.ports.candidatura.CandidaturaAdapter;
import br.com.pacto.recrutamento.app.ports.curriculo.CurriculoAdapter;
import br.com.pacto.recrutamento.app.ports.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.ports.templatevaga.TemplateVagaAdapter;
import br.com.pacto.recrutamento.app.ports.usuario.UsuarioPort;
import br.com.pacto.recrutamento.app.ports.vaga.VagaAdapter;
import br.com.pacto.recrutamento.app.serviceImpl.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppPackageStructureTest {

    @Test
    void implementacoesFicamEmServiceImpl() {
        assertPackage("br.com.pacto.recrutamento.app.serviceImpl",
                CandidatoServiceImpl.class,
                CandidaturaServiceImpl.class,
                CurriculoServiceImpl.class,
                NotificacaoServiceImpl.class,
                TemplateVagaServiceImpl.class,
                UsuarioServiceImpl.class,
                VagaServiceImpl.class);
    }

    @Test
    void portasFicamSeparadasPorEntidade() {
        assertPackage("br.com.pacto.recrutamento.app.ports.candidato",
                CandidatoAdapter.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.candidatura",
                CandidaturaAdapter.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.curriculo",
                CurriculoAdapter.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.notificacao",
                NotificacaoPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.templatevaga",
                TemplateVagaAdapter.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.usuario",
                UsuarioPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.vaga",
                VagaAdapter.class);
    }

    private void assertPackage(String esperado, Class<?>... tipos) {
        for (Class<?> tipo : tipos) {
            assertThat(tipo.getPackage().getName()).isEqualTo(esperado);
        }
    }
}
