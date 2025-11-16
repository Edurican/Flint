package com.edurican.flint.core.support.error;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public enum ErrorType {

    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    DEFAULT_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, ErrorCode.E400, "An unexpected error has occurred.", LogLevel.WARN),

    // User
    USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "User not found.", LogLevel.ERROR),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, ErrorCode.U2000, "User not found with that email.", LogLevel.WARN),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, ErrorCode.U2000, "Password mismatch.", LogLevel.WARN),
    USER_DUPLICATE_USERNAME(HttpStatus.CONFLICT, ErrorCode.U2000, "Username is already taken.", LogLevel.WARN),
    USER_DUPLICATE_EMAIL(HttpStatus.CONFLICT, ErrorCode.U2000, "Email is already taken.", LogLevel.WARN),

    // Follow
    FOLLOW_IS_ALREADY(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.F5000, "An unexpected error has occurred.", LogLevel.WARN),
    SELF_FOLLOW_NOT_ALLOWED (HttpStatus.BAD_REQUEST, ErrorCode.F5000, "Self-follow is not allowed.", LogLevel.WARN),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, ErrorCode.F5000, "Not following.", LogLevel.WARN),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.C6000, "Comment not found.", LogLevel.ERROR),
    INVALID_CONTENT(HttpStatus.NOT_FOUND, ErrorCode.C6000, "Invlid comment.", LogLevel.ERROR),
    COMMENT_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, ErrorCode.C6000, "Comment depth exceeded.", LogLevel.ERROR);

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
