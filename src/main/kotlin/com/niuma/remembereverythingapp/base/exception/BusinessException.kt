package com.niuma.remembereverythingapp.base.exception

class BusinessException(
  val code: Int,
  override val message: String,
) : RuntimeException(message) {
}