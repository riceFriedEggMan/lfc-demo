package com.rice.lfcdemo.exception;

public enum ErrorCode {

    SUCCESS(0, "Success"),
    UNKNOWN_ERROR(90001, "未知异常"),
    SYSTEM_ERROR(90002,"系统异常"),
    PARAMS_ERROR(90003, "请求参数错误");


    private final int code;
    private final String message;

    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
