package br.com.pacto.recrutamento.infra.vaga;

import br.com.pacto.recrutamento.app.services.VagaService;
import br.com.pacto.recrutamento.app.ports.vaga.AutorizacaoVaga;
import br.com.pacto.recrutamento.app.ports.vaga.PerguntaVagaRepositorio;
import br.com.pacto.recrutamento.app.ports.vaga.RequisitoVagaRepositorio;
import br.com.pacto.recrutamento.app.ports.vaga.VagaRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.VagaServiceImpl;
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
