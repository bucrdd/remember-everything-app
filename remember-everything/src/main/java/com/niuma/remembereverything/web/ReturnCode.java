package com.niuma.remembereverything.web;

public enum ReturnCode {

  RC_SUCCESS("0", "success")
  , RC_UNAUTHORIZED("4010", "Unauthorized")
  , RC_BAD_CREDENTIALS("4011", "Invalid username or password")
  , RC_SYSTEM_ERROR("9999", "Internal server error")
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
