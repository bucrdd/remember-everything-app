package com.niuma.remembereverything.exception;

public class SystemExpection extends BaseException {

  protected SystemExpection(String code, String message) {
    super(code, message);
  }

  protected SystemExpection(String code, String message, Throwable e) {
    super(code, message, e);
  }
}
