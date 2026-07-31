package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.springframework.http.ResponseEntity;

final class HttpResponses {
    private HttpResponses() {
    }

    static <T> ResponseEntity<TypedResponse<T>> from(TypedResponse<T> response) {
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
