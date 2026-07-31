package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.ports.vaga.AutorizacaoVaga;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;

@Configuration
class VagaConfiguration {
    @Bean
    AutorizacaoVaga autorizacaoVaga(AutorizacaoVagaJpaRepository repository) {
        return new AutorizacaoVagaJpaAdapter(repository,
                EnumSet.of(NomePapel.ADMINISTRADOR, NomePapel.RESPONSAVEL_VAGA));
    }
}
