package com.niuma.remembereverythingapp.util

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenUtil(
  @param:Value("\${jwt.secret}") private val secret: String,
  @param:Value("\${jwt.expiration}") private val expiration: Long,
  @param:Value("\${jwt.clock-skew}") private val clockSkew: Long,
) {

  private val log = LoggerFactory.getLogger(JwtTokenUtil::class.java)

  private val key: SecretKey = Jwts.SIG.HS512.key().build()

  fun generateToken(userDetails: UserDetails): String {
    val claims = HashMap<String, Any>()
    claims["roles"] = userDetails.authorities.map { it.authority }
    return Jwts.builder()
      .claims().empty().add(claims)
      .and()
      .subject(userDetails.username)
      .issuedAt(Date())
      .expiration(Date(System.currentTimeMillis() + expiration * 1000))
      .signWith(key)
      .compact()
  }

  fun getUsernameFromToken(token: String): String? {
    return try {
      getParser().parseSignedClaims(token).payload.subject
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
    return getParser().parseSignedClaims(token).payload.expiration
  }

  private fun getParser(): JwtParser {
    return Jwts.parser()
      .verifyWith(key)
      .clockSkewSeconds(clockSkew)
      .build()
  }
}