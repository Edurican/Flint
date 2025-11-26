package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import com.edurican.flint.core.support.response.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ApiResult<?>> handleCoreException(CoreException e) {
        // 테스트용 로그 sout
        System.out.println("CoreException 상태코드 예정: " + e.getErrorType().getStatus());
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("CoreException : {}", e.getMessage(), e);
            case WARN -> log.warn("CoreException : {}", e.getMessage(), e);
            default -> log.info("CoreException : {}", e.getMessage(), e);
        }
        return new ResponseEntity<>(ApiResult.error(e.getErrorType(), e.getData()), e.getErrorType().getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException : {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(ErrorType.DEFAULT_ARGUMENT_NOT_VALID), ErrorType.DEFAULT_ARGUMENT_NOT_VALID.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleException(Exception e) {
        // 테스트용 로그 sout
        System.out.println(" Exception 핸들러 실행됨");
        log.error("Exception : {}", e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(ErrorType.DEFAULT_ERROR), ErrorType.DEFAULT_ERROR.getStatus());
    }
}
