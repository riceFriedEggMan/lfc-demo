package com.rice.lfcdemo.model;

import java.io.Serializable;

public class ResponseEntity<T> implements Serializable {
    private int code;
    private String message;
    private String datetime;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseEntity<T> ok(T data) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>();
        responseEntity.setData(data);
        responseEntity.setCode(ResponseEnum.OK.getCode());
        responseEntity.setMessage(ResponseEnum.OK.getMessage());
        return responseEntity;
    }

    public static <T> ResponseEntity<T> ok() {
        ResponseEntity<T> responseEntity = new ResponseEntity<>();
        responseEntity.setCode(ResponseEnum.OK.getCode());
        responseEntity.setMessage(ResponseEnum.OK.getMessage());
        return responseEntity;
    }

    public static <T> ResponseEntity<T> fail(String message) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>();
        responseEntity.setCode(ResponseEnum.FAIL.getCode());
        responseEntity.setMessage(message);
        return responseEntity;
    }

    public static <T> ResponseEntity<T> fail(ResponseEnum responseEnum) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>();
        responseEntity.setCode(responseEnum.getCode());
        responseEntity.setMessage(responseEnum.getMessage());
        return responseEntity;
    }

    public static <T> ResponseEntity<T> failBusinessException(int code, String msg) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>();
        responseEntity.setMessage(msg);
        responseEntity.setCode(code);
        return responseEntity;
    }


}
