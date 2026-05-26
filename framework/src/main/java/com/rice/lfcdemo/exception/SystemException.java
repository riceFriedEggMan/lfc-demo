package com.rice.lfcdemo.exception;

import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;

public class SystemException extends RuntimeException {
    int code;
    String message;
    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public SystemException(AppHttpCodeEnum appHttpCodeEnum) {
        super(appHttpCodeEnum.getMessage());
        this.code = appHttpCodeEnum.getCode();
        this.message = appHttpCodeEnum.getMessage();
    }
}
