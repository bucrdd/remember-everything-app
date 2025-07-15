package com.niuma.remembereverythingapp.web

import com.niuma.remembereverythingapp.base.exception.BusinessException
import com.niuma.remembereverythingapp.base.response.ResultCode
import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.repository.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class UserController(
  private val userRepository: UserRepository,
) {

  @GetMapping("/me")
  fun currentUser(@AuthenticationPrincipal user: User): User {
    throw BusinessException(ResultCode.INTERNAL_SERVER_ERROR.code, ResultCode.INTERNAL_SERVER_ERROR.message)
  }

  @GetMapping("users")
  fun users(): List<User> {
    return userRepository.findAll()
  }
}