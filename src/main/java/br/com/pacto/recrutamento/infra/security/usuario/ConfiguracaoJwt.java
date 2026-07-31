package br.com.pacto.recrutamento.infra.security.usuario;

import br.com.pacto.recrutamento.app.ports.usuario.GeradorToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoJwt {
    @Bean
    GeradorToken geradorToken(@Value("$" + "{security.jwt.secret}") String segredo) {
        return new GeradorTokenJwt(segredo);
    }
}
