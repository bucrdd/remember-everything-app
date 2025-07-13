package com.niuma.remembereverythingapp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthenticationConfig {

  @Bean
  fun authenticationManager(
    http: HttpSecurity,
    userDetailsService: UserDetailsService,
    passwordEncoder: PasswordEncoder
  ): AuthenticationManager {
    val managerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
    managerBuilder
      .userDetailsService(userDetailsService)
      .passwordEncoder(passwordEncoder)
    return managerBuilder.build()
  }

}