package br.com.pacto.recrutamento.infra.usuario;

import br.com.pacto.recrutamento.core.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class GeradorTokenJwtTest {
    private static final String SEGREDO = "uma-chave-de-teste-com-pelo-menos-trinta-e-dois-bytes";

    @Test
    void accessTokenAssinadoContemUsuarioEExpiracaoInformada() {
        Usuario usuario = new Usuario("pessoa@exemplo.com", "hash");
        OffsetDateTime expiraEm = OffsetDateTime.now().plusMinutes(15);
        GeradorTokenJwt gerador = new GeradorTokenJwt(SEGREDO);

        String token = gerador.gerarAccessToken(usuario, expiraEm);

        assertThat(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SEGREDO.getBytes())).build()
                .parseSignedClaims(token).getPayload().getSubject()).isEqualTo(usuario.getId().toString());
        assertThat(Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SEGREDO.getBytes())).build()
                .parseSignedClaims(token).getPayload().getExpiration()).isEqualTo(Date.from(expiraEm.withNano(0).toInstant()));
    }

    @Test
    void tokenAleatorioEHashSeguroNaoExibemOValorPuro() {
        GeradorTokenJwt gerador = new GeradorTokenJwt(SEGREDO);
        String token = gerador.gerarTokenAleatorio();

        assertThat(gerador.calcularHash(token)).isNotEqualTo(token).hasSize(64);
        assertThat(gerador.gerarTokenAleatorio()).isNotEqualTo(token);
    }

    @Test
    void segredoAusenteOuFracoFalhaRapidamente() {
        assertThatIllegalArgumentException().isThrownBy(() -> new GeradorTokenJwt(null));
        assertThatIllegalArgumentException().isThrownBy(() -> new GeradorTokenJwt("curto"));
    }
}
