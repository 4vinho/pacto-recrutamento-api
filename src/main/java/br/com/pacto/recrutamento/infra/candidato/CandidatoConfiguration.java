package br.com.pacto.recrutamento.infra.candidato;

import br.com.pacto.recrutamento.app.ports.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.serviceImpl.CandidatoServiceImpl;
import br.com.pacto.recrutamento.app.services.CandidatoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CandidatoConfiguration {

    @Bean
    CandidatoService candidatoService(CandidatoRepository candidatoRepository) {
        return new CandidatoServiceImpl(candidatoRepository);
    }
}
