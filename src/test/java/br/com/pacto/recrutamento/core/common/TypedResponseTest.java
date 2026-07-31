package br.com.pacto.recrutamento.core.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class TypedResponseTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void respostaCalculaErroPeloStatusHttp() {
        TypedResponse<String> success = new TypedResponse<>(200, "Consulta concluída.", "resultado");
        TypedResponse<String> failure = new TypedResponse<>(404, "Recurso não encontrado.", null);

        assertThat(success.getIsError()).isFalse();
        assertThat(success.getErrorCode()).isNull();
        assertThat(failure.getIsError()).isTrue();
        assertThat(failure.getErrorCode()).isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    void respostaPermiteCodigoDeErroExplicito() {
        TypedResponse<Void> response = new TypedResponse<>(
                422, "Campo inválido.", null, ErrorCode.VALIDATION_ERROR);

        assertThat(response.getErrorCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void serializaSomenteDataEmCasoDeSucesso() throws JsonProcessingException {
        TypedResponse<String> response = new TypedResponse<>(200, "Consulta concluída.", "resultado");

        assertThat(objectMapper.writeValueAsString(response))
                .isEqualTo("{\"data\":\"resultado\"}");
    }

    @Test
    void serializaSomenteMensagemEmCasoDeErro() throws JsonProcessingException {
        TypedResponse<Void> response = new TypedResponse<>(
                401, "Não autenticado.", null, ErrorCode.AUTHENTICATION_REQUIRED);

        assertThat(objectMapper.writeValueAsString(response))
                .isEqualTo("{\"message\":\"Não autenticado.\"}");
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
