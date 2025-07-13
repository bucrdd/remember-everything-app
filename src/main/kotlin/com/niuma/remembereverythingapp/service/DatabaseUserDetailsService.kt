package com.niuma.remembereverythingapp.service

import com.niuma.remembereverythingapp.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DatabaseUserDetailsService(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails {
    val user = userRepository.findByUsername(username)
      ?: throw UsernameNotFoundException("User $username not found")
    return User.withUsername(username)
      .password(user.password)
      .authorities(user.authorities)
      .build()
  }
}