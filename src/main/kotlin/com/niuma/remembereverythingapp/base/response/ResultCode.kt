package com.niuma.remembereverythingapp.base.response

enum class ResultCode(
  val code: Int,
  val message: String,
) {
  SUCCESS(200, "Success")
  ,BAD_REQUEST(400, "Bad request")
  ,BAD_CREDENTIALS(410, "Bad credentials")
  ,INTERNAL_SERVER_ERROR(500, "Internal Server Error")
  ;
}