package com.niuma.remembereverythingapp.web

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
  repository: UserRepository,
) {

  @GetMapping("/me")
  fun currentUser(@AuthenticationPrincipal user: User): User {
    return user
  }

  @GetMapping("users")
  fun users(): List<User> {
    return userRepository.findAll()
  }
}