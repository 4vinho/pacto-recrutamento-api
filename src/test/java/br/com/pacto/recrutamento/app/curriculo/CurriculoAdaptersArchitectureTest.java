package br.com.pacto.recrutamento.app.ports.curriculo;

import br.com.pacto.recrutamento.infra.adapters.curriculo.CandidatoConsultaJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.curriculo.CurriculoRepositorioJpaAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurriculoAdaptersArchitectureTest {

    @Test
    void persistenciaDeCurriculoFicaNaInfraestrutura() {
        assertThat(CurriculoAdapter.class)
                .isAssignableFrom(CurriculoRepositorioJpaAdapter.class);
        assertThat(CandidatoConsulta.class)
                .isAssignableFrom(CandidatoConsultaJpaAdapter.class);
    }
}
