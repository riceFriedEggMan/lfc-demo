package com.rice.lfcdemo.exception;

public class BusinessException extends RuntimeException {

  private final int code;

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
  }
}
