package br.com.pacto.recrutamento.app;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.serviceImpl.CandidatoServiceImpl;
import br.com.pacto.recrutamento.app.ports.candidatura.CandidaturaRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.CandidaturaServiceImpl;
import br.com.pacto.recrutamento.app.ports.curriculo.CurriculoRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.CurriculoServiceImpl;
import br.com.pacto.recrutamento.app.ports.notificacao.NotificacaoPort;
import br.com.pacto.recrutamento.app.serviceImpl.NotificacaoServiceImpl;
import br.com.pacto.recrutamento.app.ports.templatevaga.TemplateVagaRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.TemplateVagaServiceImpl;
import br.com.pacto.recrutamento.app.ports.usuario.UsuarioPort;
import br.com.pacto.recrutamento.app.serviceImpl.UsuarioServiceImpl;
import br.com.pacto.recrutamento.app.ports.vaga.VagaRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;
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
                CandidatoRepository.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.candidatura",
                CandidaturaRepositorio.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.curriculo",
                CurriculoRepositorio.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.notificacao",
                NotificacaoPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.templatevaga",
                TemplateVagaRepositorio.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.usuario",
                UsuarioPort.class);
        assertPackage("br.com.pacto.recrutamento.app.ports.vaga",
                VagaRepositorio.class);
    }

    private void assertPackage(String esperado, Class<?>... tipos) {
        for (Class<?> tipo : tipos) {
            assertThat(tipo.getPackage().getName()).isEqualTo(esperado);
        }
    }
}
