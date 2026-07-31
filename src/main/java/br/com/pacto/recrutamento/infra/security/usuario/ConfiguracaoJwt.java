package br.com.pacto.recrutamento.infra.security.usuario;

import br.com.pacto.recrutamento.app.ports.out.usuario.GeradorTokenPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoJwt {
    @Bean
    GeradorTokenPort geradorToken(@Value("$" + "{security.jwt.secret}") String segredo) {
        return new GeradorTokenJwt(segredo);
    }
}
