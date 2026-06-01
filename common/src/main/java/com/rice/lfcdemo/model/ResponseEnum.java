package com.rice.lfcdemo.model;

public enum ResponseEnum {

    OK(0, "ok"),
    FAIL(1, "fail"),
    /**
     * 用于直接显示提示用户的错误，内容由输入内容决定
     */
    SHOW_FAIL(1, ""),
    ;


    private final int code;
    private final String message;

    private ResponseEnum(int code, String message) {
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
