package com.niuma.remembereverythingapp.base.response

import com.niuma.remembereverythingapp.base.exception.BusinessException
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors


@RestControllerAdvice
class BusinessExceptionHandler {

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

  @ExceptionHandler(AuthenticationException::class)
  fun handleAuthenticationException(e: AuthenticationException): Result<Nothing> {
    return when(e) {
      is UsernameNotFoundException, is BadCredentialsException
         -> Result.error(ResultCode.BAD_CREDENTIALS.code, e.message ?: ResultCode.BAD_CREDENTIALS.message)
      is AccountExpiredException -> Result.error(ResultCode.ACCOUNT_EXPIRED.code, e.message ?: ResultCode.ACCOUNT_EXPIRED.message)
      is LockedException -> Result.error(ResultCode.ACCOUNT_LOCKED.code, e.message ?: ResultCode.ACCOUNT_LOCKED.message)
      is DisabledException -> Result.error(ResultCode.ACCOUNT_DISABLED.code, e.message ?: ResultCode.ACCOUNT_DISABLED.message)
      else -> Result.error(ResultCode.BAD_AUTHENTICATION.code, e.message ?: ResultCode.BAD_AUTHENTICATION.message)
    }
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