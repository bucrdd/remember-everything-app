package com.niuma.remembereverythingapp.filter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.niuma.remembereverythingapp.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JsonUsernamePasswordAuthenticationFilter(
  private val authenticationManager: AuthenticationManager,
) : UsernamePasswordAuthenticationFilter() {

  private val log = logger()

  private val objectMapper: ObjectMapper = ObjectMapper()

  init {
    super.setAuthenticationManager(authenticationManager)
    setFilterProcessesUrl("/api/auth/login")
  }

  override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
    return try {
      val credentials = request.inputStream.use {
        objectMapper.readValue<LoginRequest>(it)
      }
      val authRequest = UsernamePasswordAuthenticationToken(
        credentials.username,
        credentials.password,
      )
      setDetails(request, authRequest)
      authenticationManager.authenticate(authRequest)
    } catch (e: Exception) {
      log.error("Authentication failed: {}", e.message, e)
      throw AuthenticationServiceException("Failed to parse authentication request", e)
    }
  }
}

data class LoginRequest(
  @JsonProperty("username") val username: String,
  @JsonProperty("password") val password: String
)