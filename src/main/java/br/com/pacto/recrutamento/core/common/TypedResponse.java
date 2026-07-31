package br.com.pacto.recrutamento.core.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypedResponse<T> {
    private final int statusCode;
    private final String message;
    private final String errorCode;
    private final T data;

    public TypedResponse(int statusCode, String message, T data) {
        this(statusCode, message, data, ErrorCode.fromHttpStatus(statusCode));
    }

    public TypedResponse(int statusCode, String message, T data, ErrorCode errorCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.errorCode = errorCode == null ? null : errorCode.name();
        this.data = data;
    }

    @JsonIgnore
    public int getStatusCode() {
        return statusCode;
    }

    @JsonIgnore
    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public String getErrorCode() {
        return errorCode;
    }

    @JsonIgnore
    public T getData() {
        return data;
    }

    @JsonIgnore
    public boolean getIsError() {
        return statusCode >= 400;
    }

    @JsonGetter("message")
    String serializedMessage() {
        return getIsError() ? message : null;
    }

    @JsonGetter("data")
    T serializedData() {
        return getIsError() ? null : data;
    }
}
