package br.com.pacto.recrutamento.web;

import br.com.pacto.recrutamento.core.common.TypedResponse;
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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<TypedResponse<Map<String, String>>> validation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return error(HttpStatus.UNPROCESSABLE_ENTITY, DADOS_INVALIDOS, errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    ResponseEntity<TypedResponse<Void>> malformed(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, REQUISICAO_INVALIDA, null);
    }

    @ExceptionHandler(CurriculoController.InvalidRequestException.class)
    ResponseEntity<TypedResponse<Void>> invalidFile(CurriculoController.InvalidRequestException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(), null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<TypedResponse<Void>> uploadTooLarge(MaxUploadSizeExceededException exception) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ARQUIVO_MUITO_GRANDE, null);
    }

    @ExceptionHandler(AuthenticatedUser.UnauthenticatedException.class)
    ResponseEntity<TypedResponse<Void>> unauthenticated(AuthenticatedUser.UnauthenticatedException exception) {
        return error(HttpStatus.UNAUTHORIZED, NAO_AUTENTICADO, null);
    }

    private <T> ResponseEntity<TypedResponse<T>> error(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(new TypedResponse<T>(status.value(), message, data));
    }
}
