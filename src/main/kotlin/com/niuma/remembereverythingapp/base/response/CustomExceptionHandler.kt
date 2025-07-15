package com.niuma.remembereverythingapp.base.response

import com.niuma.remembereverythingapp.base.exception.BusinessException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors


@RestControllerAdvice
class CustomExceptionHandler {

  @ExceptionHandler(BusinessException::class)
  fun handleBusinessException(e: BusinessException): Result<Nothing> {
    return Result.error(e.code, e.message)
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): Result<Nothing> {
    val message: String = e.bindingResult.allErrors
      .stream()
      .map { it.defaultMessage }
      .collect(Collectors.joining(", "))
    return Result.error(ResultCode.BAD_REQUEST.code, message)
  }

  @ExceptionHandler(Exception::class)
  fun handleException(e: Exception): Result<Nothing> {
    return when (e) {
      is IllegalArgumentException -> Result.error(ResultCode.BAD_REQUEST.code, e.message ?: "Illegal Argument")
      is IllegalStateException -> Result.error(ResultCode.BAD_REQUEST.code, e.message ?: "Illegal State")
      is NoSuchElementException -> Result.error(ResultCode.BAD_REQUEST.code, e.message ?: "No such element")
      else -> Result.error(ResultCode.INTERNAL_SERVER_ERROR.code, e.message ?: ResultCode.INTERNAL_SERVER_ERROR.message)
    }
  }
}