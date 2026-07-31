package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.core.common.BusinessException;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.LinkedHashMap;
import java.util.Map;

import static br.com.pacto.recrutamento.core.common.ErrorMessages.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<TypedResponse<Map<String, String>>> validation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR, DADOS_INVALIDOS, errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    ResponseEntity<TypedResponse<Void>> malformed(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, REQUISICAO_INVALIDA, null);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<TypedResponse<Void>> business(BusinessException exception) {
        return error(HttpStatus.valueOf(exception.getStatusCode()), exception.getErrorCode(),
                exception.getMessage(), null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<TypedResponse<Void>> uploadTooLarge(MaxUploadSizeExceededException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR, ARQUIVO_MUITO_GRANDE, null);
    }

    @ExceptionHandler(AuthenticatedUser.UnauthenticatedException.class)
    ResponseEntity<TypedResponse<Void>> unauthenticated(AuthenticatedUser.UnauthenticatedException exception) {
        return error(HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_REQUIRED, NAO_AUTENTICADO, null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<TypedResponse<Void>> unexpected(Exception exception) {
        LOGGER.error("Erro inesperado ao processar a requisição", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, ERRO_INTERNO, null);
    }

    private <T> ResponseEntity<TypedResponse<T>> error(
            HttpStatus status, ErrorCode errorCode, String message, T data) {
        return ResponseEntity.status(status)
                .body(new TypedResponse<T>(status.value(), message, data, errorCode));
    }
}
