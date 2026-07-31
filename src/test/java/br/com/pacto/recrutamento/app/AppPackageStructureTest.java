package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.ports.out.candidato.CandidatoPort;
import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CurriculoPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.TemplateVagaPort;
import br.com.pacto.recrutamento.app.ports.out.usuario.UsuarioPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
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
        assertPackage("br.com.pacto.recrutamento.app.ports.out.candidato",
                CandidatoPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.candidatura",
                CandidaturaPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.curriculo",
                CurriculoPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.notificacao",
                NotificacaoPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.templatevaga",
                TemplateVagaPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.usuario",
                UsuarioPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.out.vaga",
                VagaPort.class);
    }

    private void assertPackage(String esperado, Class<?>... tipos) {
        for (Class<?> tipo : tipos) {
            assertThat(tipo.getPackage().getName()).isEqualTo(esperado);
        }
    }
}
