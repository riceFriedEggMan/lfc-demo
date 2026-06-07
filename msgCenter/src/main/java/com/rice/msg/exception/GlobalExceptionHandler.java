package com.rice.msg.exception;

import com.rice.lfcdemo.model.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException e) {
        return ResponseEntity.failBusinessException(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.fail();
    }
}
