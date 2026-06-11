package com.niuma.remembereverythingapp.web

import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.service.AuthenticationService
import com.niuma.remembereverythingapp.util.JwtTokenUtil
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
  private val jwtTokenUtil: JwtTokenUtil
) {

  private val log = LoggerFactory.getLogger(AuthenticationController::class.java)

  @PostMapping("/login")
  fun authorize(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): LoginResponse? {
    log.info("Authenticating user: ${loginRequest.username}")
    val user = authenticationService.authenticate(loginRequest.username, loginRequest.password)
    log.info("user[${user.username}] successfully authenticated")
    return LoginResponse(
      user = user,
      token = jwtTokenUtil.generateToken(user)
    )
  }
}

data class LoginRequest(
  val username: String,
  val password: String
)

data class LoginResponse(
  val user: User,
  val token: String
)
