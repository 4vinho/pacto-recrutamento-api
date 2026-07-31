package br.com.pacto.recrutamento.infra.configurations.vaga;

import br.com.pacto.recrutamento.infra.adapters.vaga.AutorizacaoVagaJpaAdapter;

import br.com.pacto.recrutamento.infra.repositorys.vaga.AutorizacaoVagaJpaRepository;

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
