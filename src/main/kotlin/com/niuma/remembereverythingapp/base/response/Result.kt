package com.niuma.remembereverythingapp.base.response

data class Result<T>(
  val code: Int,
  val message: String,
  val data: T? = null,
  val timestamp: Long = System.currentTimeMillis(),
) {

  constructor(resultCode: ResultCode, data: T? = null)
      : this(code = resultCode.code, message = resultCode.message, data = data)

  companion object {

    fun <T> ok(data: T? = null): Result<T> {
      return Result(ResultCode.SUCCESS, data)
    }

//    fun <T> error(code: Int, message: String): Result<T> {
//      return Result(code, message, null)
//    }

    fun error(code: Int, message: String): Result<Nothing> {
      return Result(code, message, null)
    }
  }

}