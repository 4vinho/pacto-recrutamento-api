package br.com.pacto.recrutamento.infra.configurations.candidatura;

import br.com.pacto.recrutamento.app.ports.candidatura.AutorizacaoResponsavelCandidatura;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CandidaturaConfiguration {
    @Bean
    AutorizacaoResponsavelCandidatura autorizacaoResponsavelCandidatura() {
        return (usuarioId, vaga) -> usuarioId != null && usuarioId.equals(vaga.getResponsavelId());
    }
}
