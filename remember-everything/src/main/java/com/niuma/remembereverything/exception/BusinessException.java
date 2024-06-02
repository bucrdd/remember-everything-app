package com.niuma.remembereverything.exception;

import com.niuma.remembereverything.web.ReturnCode;

public class BusinessException extends BaseException {

  public BusinessException(ReturnCode rc) {
    super(rc);
  }
  public BusinessException(ReturnCode rc, Throwable e) {
    super(rc, e);
  }

  public BusinessException(String code, String message) {
    super(code, message);
  }

  public BusinessException(String code, String message, Throwable e) {
    super(code, message, e);
  }
}
