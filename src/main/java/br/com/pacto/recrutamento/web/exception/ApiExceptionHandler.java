package br.com.pacto.recrutamento.web.exception;

import br.com.pacto.recrutamento.core.common.BusinessException;
import br.com.pacto.recrutamento.core.common.ErrorCode;
import br.com.pacto.recrutamento.core.common.TypedResponse;
import br.com.pacto.recrutamento.web.security.AuthenticatedUser;
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
        LOGGER.warn("Requisicao rejeitada por validacao: campos={}", errors.keySet());
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR, DADOS_INVALIDOS, errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    ResponseEntity<TypedResponse<Void>> malformed(Exception exception) {
        LOGGER.warn("Requisicao malformada: tipo={}, causa={}",
                exception.getClass().getSimpleName(), exception.getMessage());
        return error(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST, REQUISICAO_INVALIDA, null);
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<TypedResponse<Void>> business(BusinessException exception) {
        LOGGER.warn("Regra de negocio rejeitou a requisicao: codigo={}, status={}",
                exception.getErrorCode(), exception.getStatusCode());
        return error(HttpStatus.valueOf(exception.getStatusCode()), exception.getErrorCode(),
                exception.getMessage(), null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<TypedResponse<Void>> uploadTooLarge(MaxUploadSizeExceededException exception) {
        LOGGER.warn("Upload rejeitado por exceder o tamanho permitido");
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_ERROR, ARQUIVO_MUITO_GRANDE, null);
    }

    @ExceptionHandler(AuthenticatedUser.UnauthenticatedException.class)
    ResponseEntity<TypedResponse<Void>> unauthenticated(AuthenticatedUser.UnauthenticatedException exception) {
        LOGGER.warn("Acesso rejeitado por ausencia de autenticacao");
        return error(HttpStatus.UNAUTHORIZED, ErrorCode.AUTHENTICATION_REQUIRED, NAO_AUTENTICADO, null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<TypedResponse<Void>> unexpected(Exception exception) {
        LOGGER.error("Erro inesperado ao processar a requisicao: causa={}",
                exception.getClass().getSimpleName(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, ERRO_INTERNO, null);
    }

    private <T> ResponseEntity<TypedResponse<T>> error(
            HttpStatus status, ErrorCode errorCode, String message, T data) {
        return ResponseEntity.status(status)
                .body(new TypedResponse<T>(status.value(), message, data, errorCode));
    }
}
