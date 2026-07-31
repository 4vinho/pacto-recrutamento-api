package br.com.pacto.recrutamento.infra.configurations;

import br.com.pacto.recrutamento.app.ports.out.candidatura.AutorizacaoResponsavelCandidaturaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CandidaturaConfiguration {
    @Bean
    AutorizacaoResponsavelCandidaturaPort autorizacaoResponsavelCandidatura() {
        return (usuarioId, vaga) -> vaga != null && vaga.possuiResponsavel(usuarioId);
    }
}
