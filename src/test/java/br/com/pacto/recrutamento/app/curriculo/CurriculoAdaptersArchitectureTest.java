package br.com.pacto.recrutamento.app.ports.curriculo;

import br.com.pacto.recrutamento.infra.curriculo.CandidatoConsultaJpaAdapter;
import br.com.pacto.recrutamento.infra.curriculo.CurriculoRepositorioJpaAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurriculoAdaptersArchitectureTest {

    @Test
    void persistenciaDeCurriculoFicaNaInfraestrutura() {
        assertThat(CurriculoRepositorio.class)
                .isAssignableFrom(CurriculoRepositorioJpaAdapter.class);
        assertThat(CandidatoConsulta.class)
                .isAssignableFrom(CandidatoConsultaJpaAdapter.class);
    }
}
