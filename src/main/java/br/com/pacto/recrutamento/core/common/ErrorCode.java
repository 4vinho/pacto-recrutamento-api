package br.com.pacto.recrutamento.core.common;

public enum ErrorCode {
    INVALID_REQUEST,
    VALIDATION_ERROR,
    AUTHENTICATION_REQUIRED,
    ACCESS_DENIED,
    RESOURCE_NOT_FOUND,
    CONFLICT,
    BUSINESS_RULE_VIOLATION,
    INTERNAL_ERROR;

    public static ErrorCode fromHttpStatus(int statusCode) {
        switch (statusCode) {
            case 400:
                return INVALID_REQUEST;
            case 401:
                return AUTHENTICATION_REQUIRED;
            case 403:
                return ACCESS_DENIED;
            case 404:
                return RESOURCE_NOT_FOUND;
            case 409:
                return CONFLICT;
            case 422:
                return BUSINESS_RULE_VIOLATION;
            default:
                return statusCode >= 500 ? INTERNAL_ERROR : null;
        }
    }
}
