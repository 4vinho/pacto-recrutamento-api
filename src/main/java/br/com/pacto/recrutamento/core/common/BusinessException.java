package br.com.pacto.recrutamento.core.common;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.ERROR_CODE_OBRIGATORIO;
import static br.com.pacto.recrutamento.core.common.ErrorMessages.EXCECAO_NEGOCIO_STATUS_INVALIDO;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final ErrorCode errorCode;

    public BusinessException(int statusCode, ErrorCode errorCode, String message) {
        super(message);
        if (statusCode < 400 || statusCode > 499) {
            throw new IllegalArgumentException(EXCECAO_NEGOCIO_STATUS_INVALIDO);
        }
        if (errorCode == null) {
            throw new IllegalArgumentException(ERROR_CODE_OBRIGATORIO);
        }
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
