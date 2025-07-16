package com.niuma.remembereverythingapp.config

import com.niuma.remembereverythingapp.base.response.Result
import com.niuma.remembereverythingapp.base.response.ResultCode
import com.niuma.remembereverythingapp.filter.JsonUsernamePasswordAuthenticationFilter
import com.niuma.remembereverythingapp.filter.JwtTokenRequestFilter
import com.niuma.remembereverythingapp.util.JsonUtils
import com.niuma.remembereverythingapp.util.JwtTokenUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.DelegatingSecurityContextRepository
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository

@Configuration
@EnableWebSecurity
class SecurityConfig(

) {

  private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    jwtTokenUtil: JwtTokenUtil,
    userDetailsService: UserDetailsService
  ): SecurityFilterChain {
    http {
      addFilterAt<UsernamePasswordAuthenticationFilter>(
        JwtTokenRequestFilter(jwtTokenUtil = jwtTokenUtil, userDetailsService = userDetailsService)
      )
      authorizeHttpRequests {
        authorize("/api/auth/login", permitAll)
        authorize("/**", authenticated)
      }
      cors { }
      formLogin { disable() }
      httpBasic { disable() }
      csrf { disable() }
      securityContext {
        securityContextRepository = DelegatingSecurityContextRepository(
          RequestAttributeSecurityContextRepository(),
          HttpSessionSecurityContextRepository()
        )
      }
      sessionManagement {
        sessionCreationPolicy = SessionCreationPolicy.IF_REQUIRED
        sessionFixation { changeSessionId() }
        sessionConcurrency {
          maximumSessions = 1
          maxSessionsPreventsLogin = true
        }
      }
    }
    return http.build()
  }

  //  @Bean
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
      request.session.setAttribute("username", authentication.name)
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.writer.write(
        JsonUtils.toJson(Result.ok(
          mapOf("username" to authentication.name, "roles" to authentication.authorities.map { it.authority })
        ))
      )
    }
  }

  fun authenticationFailureHandler(): AuthenticationFailureHandler {
    return AuthenticationFailureHandler { _, response, exception ->
      log.error("Authentication Failed: {}", exception.message)
//      response.status = HttpStatus.UNAUTHORIZED.value()
      response.contentType = MediaType.APPLICATION_JSON_VALUE
      response.writer.write(JsonUtils.toJson(Result.error(
        ResultCode.BAD_REQUEST.code,
        exception.message ?: ResultCode.BAD_REQUEST.message,
      )))
    }
  }
}