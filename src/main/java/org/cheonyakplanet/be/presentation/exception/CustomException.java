package org.cheonyakplanet.be.presentation.exception;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String details;

    public CustomException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetails() {
        return details;
    }
}
