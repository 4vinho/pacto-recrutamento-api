package br.com.pacto.recrutamento.web.support;

import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.ResponseEntity;

public final class HttpResponses {
    private HttpResponses() {
    }

    public static <T> ResponseEntity<TypedResponse<T>> from(TypedResponse<T> response) {
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
