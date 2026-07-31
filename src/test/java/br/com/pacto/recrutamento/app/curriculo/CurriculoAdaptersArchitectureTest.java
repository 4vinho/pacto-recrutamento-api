package br.com.pacto.recrutamento.app.ports.out.curriculo;

import br.com.pacto.recrutamento.infra.adapters.curriculo.CandidatoConsultaJpaAdapter;
import br.com.pacto.recrutamento.infra.adapters.curriculo.CurriculoRepositorioJpaAdapter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurriculoAdaptersArchitectureTest {

    @Test
    void persistenciaDeCurriculoFicaNaInfraestrutura() {
        assertThat(CurriculoPort.class)
                .isAssignableFrom(CurriculoRepositorioJpaAdapter.class);
        assertThat(CandidatoConsultaPort.class)
                .isAssignableFrom(CandidatoConsultaJpaAdapter.class);
    }
}
