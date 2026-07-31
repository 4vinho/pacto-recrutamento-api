package br.com.pacto.recrutamento.app.ports.out.usuario;

import br.com.pacto.recrutamento.app.dtos.usuario.CadastrarUsuarioDTO;
import br.com.pacto.recrutamento.app.dtos.usuario.RedefinirSenhaDTO;
import br.com.pacto.recrutamento.app.usecases.usuario.UsuarioService;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class UsuarioServiceSenhaTest {
    private final UsuarioPort usuarios = mock(UsuarioPort.class);
    private final PapelPort papeis = mock(PapelPort.class);
    private final RefreshTokenPort refreshTokens = mock(RefreshTokenPort.class);
    private final CodificadorSenhaPort senhas = mock(CodificadorSenhaPort.class);
    private final GeradorTokenPort tokens = mock(GeradorTokenPort.class);
    private final RecuperacaoSenhaPort recuperacoes = mock(RecuperacaoSenhaPort.class);
    private final CanalRecuperacaoSenhaPort canal = mock(CanalRecuperacaoSenhaPort.class);
    private final UsuarioService service = new UsuarioService(usuarios, papeis, refreshTokens,
            senhas, tokens, recuperacoes, canal, Clock.systemUTC());

    @Test
    void cadastroRejeitaSenhaComMenosDeOitoCaracteres() {
        assertThat(service.cadastrarUsuario(
                new CadastrarUsuarioDTO("pessoa@empresa.com", "62999999999", "1234567"))
                .getStatusCode()).isEqualTo(400);

        verifyNoInteractions(usuarios, papeis, refreshTokens, senhas, tokens, recuperacoes, canal);
    }

    @Test
    void cadastroRejeitaSenhaComMaisDeSetentaEDoisCaracteres() {
        assertThat(service.cadastrarUsuario(
                new CadastrarUsuarioDTO("pessoa@empresa.com", "62999999999", repetir('a', 73)))
                .getStatusCode()).isEqualTo(400);
    }

    @Test
    void redefinicaoAplicaAMesmaPoliticaDeSenha() {
        assertThat(service.redefinirSenha(new RedefinirSenhaDTO("token", "curta"))
                .getStatusCode()).isEqualTo(400);
        assertThat(service.redefinirSenha(new RedefinirSenhaDTO("token", repetir('a', 73)))
                .getStatusCode()).isEqualTo(400);

        verifyNoInteractions(usuarios, papeis, refreshTokens, senhas, tokens, recuperacoes, canal);
    }

    private String repetir(char caractere, int quantidade) {
        StringBuilder resultado = new StringBuilder(quantidade);
        for (int i = 0; i < quantidade; i++) resultado.append(caractere);
        return resultado.toString();
    }
}
