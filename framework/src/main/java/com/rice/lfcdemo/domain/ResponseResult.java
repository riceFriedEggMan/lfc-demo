package com.rice.lfcdemo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {

    private Integer code;

    private String message;

    private T date;

    public ResponseResult(Integer code, T date) {
        this.code = code;
        this.date = date;
    }

    public ResponseResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseResult(Integer code, String message, T date) {
        this.code = code;
        this.message = message;
        this.date = date;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }

    public static ResponseResult ok() {
        return new ResponseResult(200, "OK");
    }

    public static ResponseResult ok(Object date) {
        return new ResponseResult(200, "OK", date);
    }




}
