package com.niuma.remembereverythingapp.service

import com.niuma.remembereverythingapp.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class DatabaseUserDetailsService(
  private val userRepository: UserRepository,
) : UserDetailsService {

  override fun loadUserByUsername(username: String): UserDetails {
    return userRepository.findByUsername(username)
      ?: throw UsernameNotFoundException("User $username not found")
  }
}