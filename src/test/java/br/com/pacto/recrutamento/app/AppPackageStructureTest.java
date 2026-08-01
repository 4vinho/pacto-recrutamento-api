package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.ports.out.candidatura.CandidaturaPort;
import br.com.pacto.recrutamento.app.ports.out.curriculo.CurriculoPort;
import br.com.pacto.recrutamento.app.ports.out.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.ports.out.templatevaga.TemplateVagaPort;
import br.com.pacto.recrutamento.app.ports.out.usuario.UsuarioPort;
import br.com.pacto.recrutamento.app.ports.out.vaga.VagaPort;
import br.com.pacto.recrutamento.app.usecases.candidatura.CandidaturaService;
import br.com.pacto.recrutamento.app.usecases.curriculo.CurriculoService;
import br.com.pacto.recrutamento.app.usecases.notificacao.NotificacaoService;
import br.com.pacto.recrutamento.app.usecases.templatevaga.TemplateVagaService;
import br.com.pacto.recrutamento.app.usecases.usuario.UsuarioService;
import br.com.pacto.recrutamento.app.usecases.vaga.VagaService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppPackageStructureTest {

    @Test
    void implementacoesFicamOrganizadasPorCasoDeUso() {
        assertPackage("br.com.pacto.recrutamento.app.usecases.candidatura", CandidaturaService.class);
        assertPackage("br.com.pacto.recrutamento.app.usecases.curriculo", CurriculoService.class);
        assertPackage("br.com.pacto.recrutamento.app.usecases.notificacao", NotificacaoService.class);
        assertPackage("br.com.pacto.recrutamento.app.usecases.templatevaga", TemplateVagaService.class);
        assertPackage("br.com.pacto.recrutamento.app.usecases.usuario", UsuarioService.class);
        assertPackage("br.com.pacto.recrutamento.app.usecases.vaga", VagaService.class);
    }

    @Test
    void portasFicamSeparadasPorEntidade() {
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
