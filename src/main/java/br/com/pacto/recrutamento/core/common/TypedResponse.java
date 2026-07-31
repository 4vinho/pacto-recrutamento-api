package br.com.pacto.recrutamento.core.common;

public class TypedResponse<T> {
    private final int statusCode;
    private final String message;
    private final T data;

    public TypedResponse(int statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean getIsError() {
        return statusCode >= 400;
    }
}
