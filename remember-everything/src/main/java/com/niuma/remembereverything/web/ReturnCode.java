package com.niuma.remembereverything.web;

public enum ReturnCode {

  RC_0("0", "success")
  , RC_900("900", "internal server error")
  ;

  private final String code;

  private final String message;

  ReturnCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() { return code; }

  public String getMessage() { return message; }
}
