package com.niuma.remembereverythingapp.base.response

enum class ResultCode(
  val code: Int,
  val message: String,
) {
  SUCCESS(200, "Success")
  , BAD_REQUEST(400, "Bad request")
  , BAD_AUTHENTICATION(401, "Authentication failed")
  , FORBIDDEN(403, "Forbidden")
  , BAD_CREDENTIALS(411, "Bad credentials")
  , ACCOUNT_EXPIRED(412, "Bad credentials")
  , ACCOUNT_DISABLED(413, "Bad credentials")
  , ACCOUNT_LOCKED(413, "Bad credentials")
  , INTERNAL_SERVER_ERROR(500, "Internal Server Error")
  ;
}