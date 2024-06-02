package com.niuma.remembereverything.exception;

import com.niuma.remembereverything.web.ReturnCode;
import java.io.Serial;

public class BaseException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  protected String code;

  protected BaseException(ReturnCode rc) {
    this(rc, null);
  }

  protected BaseException(ReturnCode rc, Throwable e) {
    super(rc.getMessage() + "(CODE: " +  rc.getCode() + ")", e);
    this.code = rc.getCode();
  }

  protected BaseException(String code, String message) {
    this(code, message, null);
  }

  protected BaseException(String code, String message, Throwable e) {
    super(message + "(CODE: " + code + ")", e);
    this.code = code;
  }

  public String getCode() { return code; }

}
