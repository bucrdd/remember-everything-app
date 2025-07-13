package com.niuma.remembereverythingapp.web

import org.springframework.security.core.session.SessionRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SessionController(
  private val sessionRegistry: SessionRegistry,
) {

  @GetMapping("/sessions")
  fun sessions(): List<Any?>? {
    return sessionRegistry.allPrincipals
  }
}