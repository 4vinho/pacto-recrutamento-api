package br.com.pacto.recrutamento.infra.configurations;

import br.com.pacto.recrutamento.app.ports.out.candidatura.AutorizacaoResponsavelCandidaturaPort;
import br.com.pacto.recrutamento.core.enums.NomePapel;
import br.com.pacto.recrutamento.infra.repositorys.vaga.AutorizacaoVagaJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CandidaturaConfiguration {
    @Bean
    AutorizacaoResponsavelCandidaturaPort autorizacaoResponsavelCandidatura(
            AutorizacaoVagaJpaRepository repository) {
        return (usuarioId, vaga) -> vaga != null && (vaga.possuiResponsavel(usuarioId)
                || repository.possuiPapelAtivo(
                        usuarioId, java.util.Collections.singleton(NomePapel.ADMINISTRADOR)));
    }
}
