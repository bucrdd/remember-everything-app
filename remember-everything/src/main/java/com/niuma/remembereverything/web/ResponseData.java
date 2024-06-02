package com.niuma.remembereverything.web;

import static com.niuma.remembereverything.web.ReturnCode.RC_0;
import static com.niuma.remembereverything.web.ReturnCode.RC_900;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ResponseData<T> {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private ReturnCode rc;

  private String message;

  private String code;

  private T data;

  private final long timestamp;

  public ResponseData() {
    timestamp = System.currentTimeMillis();
  }

  public static <T> ResponseData<T> success(T data) {
    ResponseData<T> result = new ResponseData<>();
    result.setData(data);
    result.setCode(RC_0.getCode());
    result.setMessage(RC_0.getMessage());
    return result;
  }

  public static <T> ResponseData<T> fail(String message) {
    ResponseData<T> result = new ResponseData<>();
    result.setCode(RC_900.getCode());
    result.setMessage(message);
    return result;
  }

  public static <T> ResponseData<T> fail(String message, String code) {
    ResponseData<T> result = new ResponseData<>();
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  public static <T> ResponseData<T> fail(String message, String code, T data) {
    ResponseData<T> result = new ResponseData<>();
    result.setData(data);
    result.setCode(code);
    result.setMessage(message);
    return result;
  }

  public static <T> ResponseData<T> fail(ReturnCode code, T data) {
    ResponseData<T> result = new ResponseData<>();
    result.setData(data);
    result.setCode(code.getCode());
    result.setMessage(code.getMessage());
    return result;
  }

}
