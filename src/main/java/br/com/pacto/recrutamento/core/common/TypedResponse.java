package br.com.pacto.recrutamento.core.common;

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

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public T getData() {
        return data;
    }

    public boolean getIsError() {
        return statusCode >= 400;
    }
}
