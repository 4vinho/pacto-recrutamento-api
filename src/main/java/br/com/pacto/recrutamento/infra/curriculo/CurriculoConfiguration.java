package br.com.pacto.recrutamento.infra.curriculo;

import br.com.pacto.recrutamento.app.ports.curriculo.ArquivoStorage;
import br.com.pacto.recrutamento.app.ports.curriculo.CandidatoConsulta;
import br.com.pacto.recrutamento.app.ports.curriculo.CurriculoRepositorio;
import br.com.pacto.recrutamento.app.serviceImpl.CurriculoServiceImpl;
import br.com.pacto.recrutamento.app.ports.curriculo.RemocaoCurriculoPendente;
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
