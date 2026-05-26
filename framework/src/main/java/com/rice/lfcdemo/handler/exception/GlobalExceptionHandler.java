package com.rice.lfcdemo.handler.exception;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;
import com.rice.lfcdemo.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = SystemException.class)
    public ResponseResult systemExceptionHandler(SystemException e){
        log.error(e.getMessage());
        return ResponseResult.errorResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseResult exceptionHandler(Exception e){
        log.error(e.getMessage());
        e.printStackTrace();
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), e.getMessage());
    }
}
