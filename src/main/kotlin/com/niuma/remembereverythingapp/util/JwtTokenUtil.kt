package com.niuma.remembereverythingapp.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.expiration}") private val expiration: Long,
) {

  private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

  fun generateToken(userDetails: UserDetails): String {
    val claims = HashMap<String, Any>()
    claims.put("roles", userDetails.authorities.map { it.authority })
    return Jwts.builder()
      .setClaims(claims)
      .setSubject(userDetails.username)
      .setIssuedAt(Date())
      .setExpiration(Date(System.currentTimeMillis() + expiration * 1000))
      .signWith(key, SignatureAlgorithm.HS512)
      .compact()
  }

  fun getUsernameFromToken(token: String): String? {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject
  }

  fun validateToken(token: String, userDetails: UserDetails): Boolean {
    val username = getUsernameFromToken(token)
    return username == userDetails.username && !isTokenExpiration(token)
  }

  private fun isTokenExpiration(token: String): Boolean {
    val expirationDate = getExpirationFromToken(token)
    return expirationDate.before(Date())
  }

  private fun getExpirationFromToken(token: String): Date {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.expiration
  }
}