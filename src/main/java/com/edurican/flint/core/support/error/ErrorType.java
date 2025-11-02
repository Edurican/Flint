package com.edurican.flint.core.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "User not found.", LogLevel.ERROR),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, ErrorCode.U2000, "User not found with that email.", LogLevel.WARN),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, ErrorCode.U2000, "Password mismatch.", LogLevel.WARN),
    USER_DUPLICATE_USERNAME(HttpStatus.CONFLICT, ErrorCode.U2000, "Username is already taken.", LogLevel.WARN),
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, ErrorCode.U2000, "Email is already taken.", LogLevel.WARN),
    FOLLOW_IS_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.F5000, "An unexpected error has occurred.", LogLevel.WARN),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.C6000, "Comment not found.", LogLevel.ERROR);

    private final HttpStatus status;

    private final ErrorCode code;

    private final String message;

    private final LogLevel logLevel;

    ErrorType(HttpStatus status, ErrorCode code, String message, LogLevel logLevel) {

        this.status = status;
        this.code = code;
        this.message = message;
        this.logLevel = logLevel;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

}
