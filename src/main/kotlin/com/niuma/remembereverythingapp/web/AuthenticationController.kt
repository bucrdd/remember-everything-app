package com.niuma.remembereverythingapp.web

import com.niuma.remembereverythingapp.dto.LoginRequest
import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.service.AuthenticationService
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController (
  private val authenticationService: AuthenticationService,
) {

  private val log = LoggerFactory.getLogger(AuthenticationController::class.java)

  @PostMapping("/login")
  fun authorize(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): User? {
    log.info("Authenticating user: ${loginRequest.username}")
    val user = authenticationService.authenticate(loginRequest)
    log.info("user[${user.id}] successfully authenticated")
    return user
  }
}
