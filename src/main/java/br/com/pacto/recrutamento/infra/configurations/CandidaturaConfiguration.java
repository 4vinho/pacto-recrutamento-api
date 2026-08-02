package br.com.pacto.recrutamento.infra.configurations;

import br.com.pacto.recrutamento.app.ports.out.candidatura.AutorizacaoResponsavelCandidaturaPort;
import br.com.pacto.recrutamento.core.entities.Vaga;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import br.com.pacto.recrutamento.infra.repositorys.vaga.AutorizacaoVagaJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.UUID;

@Configuration
class CandidaturaConfiguration {
    @Bean
    AutorizacaoResponsavelCandidaturaPort autorizacaoResponsavelCandidatura(
            AutorizacaoVagaJpaRepository repository) {
        return new AutorizacaoResponsavelCandidaturaPort() {
            @Override
            public boolean podeGerenciar(UUID usuarioId, Vaga vaga) {
                return vaga != null && repository.possuiPapelAtivo(usuarioId, Arrays.asList(
                        NomePapel.ADMINISTRADOR, NomePapel.RESPONSAVEL_VAGA));
            }

            @Override
            public boolean podeConsultarCurriculos(UUID usuarioId) {
                return repository.possuiPapelAtivo(usuarioId, Arrays.asList(
                        NomePapel.ADMINISTRADOR, NomePapel.RESPONSAVEL_VAGA));
            }
        };
    }
}
