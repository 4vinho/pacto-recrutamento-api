package br.com.pacto.recrutamento.infra.candidato;

import br.com.pacto.recrutamento.app.candidato.CandidatoRepository;
import br.com.pacto.recrutamento.app.candidato.CandidatoServiceImpl;
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
