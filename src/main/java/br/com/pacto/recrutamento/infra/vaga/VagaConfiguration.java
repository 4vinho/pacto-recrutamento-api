package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.services.VagaService;
import br.com.pacto.recrutamento.app.vaga.AutorizacaoVaga;
import br.com.pacto.recrutamento.app.vaga.PerguntaVagaRepositorio;
import br.com.pacto.recrutamento.app.vaga.RequisitoVagaRepositorio;
import br.com.pacto.recrutamento.app.vaga.VagaRepositorio;
import br.com.pacto.recrutamento.app.vaga.VagaServiceImpl;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.EnumSet;

@Configuration
class VagaConfiguration {
    @Bean
    AutorizacaoVaga autorizacaoVaga(AutorizacaoVagaJpaRepository repository) {
        return new AutorizacaoVagaJpaAdapter(repository,
                EnumSet.of(NomePapel.ADMINISTRADOR, NomePapel.RESPONSAVEL_VAGA));
    }

    @Bean
    VagaService vagaService(VagaRepositorio vagas, PerguntaVagaRepositorio perguntas,
                            RequisitoVagaRepositorio requisitos, AutorizacaoVaga autorizacao) {
        return new VagaServiceImpl(vagas, perguntas, requisitos, autorizacao, Clock.systemUTC());
    }
}
