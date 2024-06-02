package com.niuma.remembereverything.web;


import com.niuma.remembereverything.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseData<String> handleException(BaseException e) {
    log.error(e.getMessage(), e);
    return ResponseData.fail(e.getMessage(), e.getCode());
  }

  @ExceptionHandler
  public ResponseData<String> handleException(Exception e) {
    log.error(e.getMessage(), e);
    return ResponseData.fail(e.getMessage());
  }
}
