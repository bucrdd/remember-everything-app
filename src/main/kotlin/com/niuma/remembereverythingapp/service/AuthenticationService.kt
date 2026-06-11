package com.niuma.remembereverythingapp.service

import com.niuma.remembereverythingapp.base.response.ResultCode
import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) {

  private val log = LoggerFactory.getLogger(AuthenticationService::class.java)

  fun authenticate(username: String, password: String): User {
    val user = userRepository.findByUsername(username)
    if (user == null) {
      log.warn("User $username not found")
      throw UsernameNotFoundException(ResultCode.BAD_CREDENTIALS.message)
    }
    if (!passwordEncoder.matches(password, user.password)) {
      log.warn("User $username does not match password")
      throw BadCredentialsException(ResultCode.BAD_CREDENTIALS.message)
    }
    return user
  }
}