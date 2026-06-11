package com.niuma.remembereverythingapp.filter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class JsonUsernamePasswordAuthenticationFilter(
  private val authenticationManager: AuthenticationManager,
) : UsernamePasswordAuthenticationFilter() {

  private val log = LoggerFactory.getLogger(JsonUsernamePasswordAuthenticationFilter::class.java)

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
      when (e) {
        is BadCredentialsException -> {
          log.error("Bad credentials: {}", e.message)
          throw BadCredentialsException(e.message ?: "Bad credentials", e)
        }

        else -> {
          log.error("Authentication failed: {}", e.message, e)
          throw AuthenticationServiceException(e.message ?: "Failed to parse authentication request", e)
        }
      }
    }
  }
}

data class LoginRequest(
    @param:JsonProperty("username") val username: String,
    @param:JsonProperty("password") val password: String
)