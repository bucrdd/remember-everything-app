package com.niuma.remembereverythingapp.web

import jakarta.servlet.http.HttpSession
import org.springframework.security.core.session.SessionRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api")
class SessionController(
  private val sessionRegistry: SessionRegistry,
) {

  @GetMapping("/sessions")
  fun sessions(): List<Any?>? {
    return sessionRegistry.allPrincipals
  }

  @GetMapping("/current-session")
  fun currentSession(session: HttpSession): Map<String, Any> {
    return mapOf(
      "id" to session.id,
      "user" to session.getAttribute("username"),
      "lastAccessed" to Instant.ofEpochMilli(session.lastAccessedTime)
    )
  }
}