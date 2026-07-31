package br.com.pacto.recrutamento.core.common;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginaGenericoTest {

    @Test
    void preservaItensETotalSemPermitirAlteracaoExterna() {
        List<String> origem = new ArrayList<>(Arrays.asList("primeiro", "segundo"));

        PaginaGenerico<String> pagina = new PaginaGenerico<>(origem, 8);
        origem.clear();

        assertThat(pagina.getItens()).containsExactly("primeiro", "segundo");
        assertThat(pagina.getTotalItens()).isEqualTo(8);
        assertThatThrownBy(() -> pagina.getItens().add("terceiro"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejeitaListaNulaETotalNegativo() {
        assertThatThrownBy(() -> new PaginaGenerico<String>(null, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PaginaGenerico<String>(new ArrayList<>(), -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
