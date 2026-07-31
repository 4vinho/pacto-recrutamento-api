package br.com.pacto.recrutamento.app.ports.out.usuario;

import br.com.pacto.recrutamento.app.usecases.usuario.UsuarioService;
import br.com.pacto.recrutamento.infra.adapters.usuario.PapelJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.usuario.RefreshTokenJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.usuario.UsuarioJpaAdapter;
import br.com.pacto.recrutamento.infra.security.usuario.BCryptCodificadorSenha;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioArquiteturaTest {

    @Test
    void casoDeUsoEPortasDeUsuarioPermanecemNaCamadaAppDaFuncionalidade() {
        assertThat(UsuarioService.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.usecases.usuario");
        assertThat(UsuarioPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(PapelPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(RefreshTokenPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(CodificadorSenhaPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(GeradorTokenPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(RecuperacaoSenhaPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
        assertThat(CanalRecuperacaoSenhaPort.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.app.ports.out.usuario");
    }

    @Test
    void adaptadoresDePersistenciaEHashPermanecemNaInfraestrutura() {
        assertThat(UsuarioJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.adapters.usuario");
        assertThat(PapelJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.adapters.usuario");
        assertThat(RefreshTokenJpaAdapter.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.adapters.usuario");
        assertThat(BCryptCodificadorSenha.class.getPackage().getName())
                .isEqualTo("br.com.pacto.recrutamento.infra.security.usuario");
    }
}
