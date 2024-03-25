package com.sparta.eroomprojectbe.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EroomException.class)
    public ResponseEntity<ExceptionResponse> handleEroomException(EroomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ExceptionResponse response = new ExceptionResponse(errorCode.getCode(), errorCode.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }
}
