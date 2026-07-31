package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.core.common.BusinessException;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.ERRO_INTERNO;
import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void trataExcecaoDeNegocioComContratoPadronizado() {
        ResponseEntity<TypedResponse<Void>> response = handler.business(
                new BusinessException(409, ErrorCode.CONFLICT, "Conflito conhecido."));

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("CONFLICT");
        assertThat(response.getBody().getMessage()).isEqualTo("Conflito conhecido.");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void ocultaDetalhesDeErroInesperado() {
        ResponseEntity<TypedResponse<Void>> response = handler.unexpected(
                new IllegalStateException("select * from usuarios senha=segredo"));

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo(ERRO_INTERNO);
        assertThat(response.getBody().getMessage()).doesNotContain("usuarios", "senha", "select");
    }
}
