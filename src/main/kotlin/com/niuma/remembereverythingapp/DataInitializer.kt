package com.niuma.remembereverythingapp

import com.niuma.remembereverythingapp.entity.User
import com.niuma.remembereverythingapp.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
  private val users: UserRepository,
  private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

  private val log = LoggerFactory.getLogger(DataInitializer::class.java)

  override fun run(vararg args: String?) {
    log.debug("Data initialization")
    val user = User(
      username = "user",
      password = passwordEncoder.encode("123456"),
      roles = setOf("ROLE_USER")
    )
    // user.username = "user"
    // user.password = passwordEncoder.encode("123456")
    // user.roles = listOf("ROLE_USER")

    val admin = User(
      username = "admin",
      password = passwordEncoder.encode("123456"),
      roles = setOf("ROLE_USER", "ROLE_ADMIN")
    )

   // admin.username = "admin"
   // admin.password = passwordEncoder.encode("123456")
   // admin.roles = listOf("ROLE_USER", "ROLE_ADMIN")

    users.saveAll(listOf(user, admin))
    users.findAll().forEach { u -> log.debug("User: {}", u.toString()) }
  }
}