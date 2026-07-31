package br.com.pacto.recrutamento.app.ports.usuario;

import br.com.pacto.recrutamento.app.serviceImpl.UsuarioServiceImpl;
import br.com.pacto.recrutamento.infra.security.usuario.BCryptCodificadorSenha;
import br.com.pacto.recrutamento.infra.adapters.usuario.PapelJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.usuario.RefreshTokenJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.usuario.UsuarioJpaAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioArquiteturaTest {

    @Test
    void casoDeUsoEPortasDeUsuarioPermanecemNaCamadaAppDaFuncionalidade() {
        assertThat(UsuarioServiceImpl.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.serviceImpl");
        assertThat(UsuarioPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
        assertThat(PapelPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
        assertThat(RefreshTokenPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
        assertThat(CodificadorSenha.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
        assertThat(GeradorToken.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
        assertThat(RecuperacaoSenhaPort.class.getPackage().getName()).isEqualTo("br.com.pacto.recrutamento.app.ports.usuario");
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
