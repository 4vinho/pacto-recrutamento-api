package br.com.pacto.recrutamento.infra.curriculo;

import br.com.pacto.recrutamento.app.curriculo.ArquivoStorage;
import br.com.pacto.recrutamento.app.curriculo.CandidatoConsulta;
import br.com.pacto.recrutamento.app.curriculo.CurriculoRepositorio;
import br.com.pacto.recrutamento.app.curriculo.CurriculoServiceImpl;
import br.com.pacto.recrutamento.app.curriculo.RemocaoCurriculoPendente;
import br.com.pacto.recrutamento.app.services.CurriculoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CurriculoConfiguration {

    @Bean
    public CurriculoService curriculoService(
            CurriculoRepositorio repositorio,
            ArquivoStorage storage,
            CandidatoConsulta candidatos,
            RemocaoCurriculoPendente remocoesPendentes,
            Clock clock) {
        return new CurriculoServiceImpl(
                repositorio, storage, candidatos, remocoesPendentes, clock);
    }
}
