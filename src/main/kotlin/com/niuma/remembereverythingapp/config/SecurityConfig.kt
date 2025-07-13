package com.niuma.remembereverythingapp.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.filter.JsonUsernamePasswordAuthenticationFilter
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val passwordEncoder: PasswordEncoder
) {

  private val objectMapper: ObjectMapper = ObjectMapper()

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    authenticationManager: ObjectProvider<AuthenticationManager>
  ): SecurityFilterChain {
    http {
      addFilterAt<UsernamePasswordAuthenticationFilter>(
        jsonUsernamePasswordAuthenticationFilter(
          authenticationManager = authenticationManager.getObject())
      )
      authorizeHttpRequests {
        authorize("/**", permitAll)
      }
      cors { }
      formLogin { disable() }
      httpBasic { disable() }
      csrf { disable() }
      sessionManagement {
        sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED // 或 ALWAYS
        sessionFixation { changeSessionId() }
        sessionConcurrency {
          maximumSessions = 1
          maxSessionsPreventsLogin = true
        }
      }

    }
    return http.build()
  }

  @Bean
  fun jsonUsernamePasswordAuthenticationFilter(
    authenticationManager: AuthenticationManager,
  ): JsonUsernamePasswordAuthenticationFilter {
    return JsonUsernamePasswordAuthenticationFilter(authenticationManager = authenticationManager).apply {
      setAuthenticationSuccessHandler(authenticationSuccessHandler())
      setAuthenticationFailureHandler(authenticationFailureHandler())
    }
  }

  fun authenticationSuccessHandler(): AuthenticationSuccessHandler {
    return AuthenticationSuccessHandler { request, response, authentication ->
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.writer.write(objectMapper.writeValueAsString(
        mapOf("username" to authentication.name, "authorities" to authentication.authorities.map { it.authority })))
    }
  }

  fun authenticationFailureHandler(): AuthenticationFailureHandler {
    return AuthenticationFailureHandler { _, response, exception ->
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.writer.write("""{"error": "Authentication failed", "message": "${exception.message}"}""")
    }
  }
}