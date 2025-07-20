package com.niuma.remembereverythingapp.util

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.expiration}") private val expiration: Long,
  @Value("\${jwt.clock-skew}") private val clockSkew: Long,
) {

  private val log = LoggerFactory.getLogger(JwtTokenUtil::class.java)

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
    return try {
      getParser().parseClaimsJws(token).body.subject
    } catch (e: ExpiredJwtException) {
      log.warn("Expired JWT for user ${e.claims.subject}", e)
      null
    } catch (e: Exception) {
      log.warn("Invalid JWT: ${e.message}", e)
      null
    }
  }

  fun validateToken(token: String, userDetails: UserDetails): Boolean {
    val username = getUsernameFromToken(token)
    return username == userDetails.username && !isTokenExpiration(token)
  }

  private fun isTokenExpiration(token: String): Boolean {
    val expirationDate = getExpirationFromToken(token)
    return expirationDate.before(Date(System.currentTimeMillis() - clockSkew * 1000))
  }

  private fun getExpirationFromToken(token: String): Date {
    return getParser().parseClaimsJws(token).body.expiration
  }

  private fun getParser(): JwtParser {
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .setAllowedClockSkewSeconds(clockSkew)
      .build()
  }
}