package br.com.pacto.recrutamento.core.common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TypedResponseTest {

    @Test
    void respostaCalculaErroPeloStatusHttp() {
        TypedResponse<String> success = new TypedResponse<>(200, "Consulta concluída.", "resultado");
        TypedResponse<String> failure = new TypedResponse<>(404, "Recurso não encontrado.", null);

        assertThat(success.getIsError()).isFalse();
        assertThat(failure.getIsError()).isTrue();
    }

    @Test
    void respostaPaginadaHerdaContratoDaRespostaECalculaTotalDePaginas() {
        TypedPagedResponse<String> response = new TypedPagedResponse<>(
                200,
                "Consulta concluída.",
                Arrays.asList("primeiro", "segundo"),
                1,
                2,
                5
        );

        assertThat(response).isInstanceOf(TypedResponse.class);
        assertThat(response.getData()).containsExactly("primeiro", "segundo");
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getTotalItems()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getIsError()).isFalse();
    }
}
