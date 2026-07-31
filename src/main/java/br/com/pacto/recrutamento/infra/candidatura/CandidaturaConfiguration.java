package br.com.pacto.recrutamento.infra.candidatura;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.ports.candidatura.*;
import br.com.pacto.recrutamento.app.serviceImpl.CandidaturaServiceImpl;
import br.com.pacto.recrutamento.app.services.CandidaturaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CandidaturaConfiguration {
    @Bean
    AutorizacaoResponsavelCandidatura autorizacaoResponsavelCandidatura() {
        return (usuarioId, vaga) -> usuarioId != null && usuarioId.equals(vaga.getResponsavelId());
    }
    @Bean
    CandidaturaService candidaturaService(CandidatoRepository candidatos,
                                           CandidaturaRepositorio candidaturas,
                                           VagaCandidaturaRepositorio vagas,
                                           PerguntaCandidaturaRepositorio perguntas,
                                           AutorizacaoResponsavelCandidatura autorizacao,
                                           EventosCandidatura eventos) {
        return new CandidaturaServiceImpl(candidatos, candidaturas, vagas, perguntas, autorizacao, eventos);
    }
}
