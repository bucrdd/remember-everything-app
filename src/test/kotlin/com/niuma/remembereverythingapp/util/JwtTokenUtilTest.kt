package com.niuma.remembereverythingapp.util

import com.niuma.remembereverythingapp.entity.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.crypto.SecretKey

class JwtTokenUtilTest {

  private lateinit var jwtTokenUtil: JwtTokenUtil
  private val secret = "testSecretKeyMustBeAtLeast32BytesLong123456testSecretKeyMustBeAtLeast32BytesLong123456"
  private val expiration = 3600L
  private val clockSkew = 30L

  @BeforeEach
  fun setup() {
    jwtTokenUtil = JwtTokenUtil(secret, expiration, clockSkew)
  }

  @Test
  fun `should generate valid token`() {
    val roles = setOf("ROLE_USER")
    val userDetails = User("testUser", "password", roles)
    val token = jwtTokenUtil.generateToken(userDetails)

    assertThat(token).isNotBlank()
    assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("testUser")
    assertThat(jwtTokenUtil.validateToken(token, userDetails)).isTrue()
  }

  @Test
  fun `should extract username from token`() {
    val userDetails = User("testUser", "password", emptySet())
    val token = jwtTokenUtil.generateToken(userDetails)
    val username = jwtTokenUtil.getUsernameFromToken(token)
    assertThat(username).isEqualTo("testUser")
  }

  @Test
  fun `should validate token with correct user`() {
    val userDetails = User("testUser", "password", emptySet())
    val token = jwtTokenUtil.generateToken(userDetails)

    assertThat(jwtTokenUtil.validateToken(token, userDetails)).isTrue()
  }

  @Test
  fun `should invalidate token with incorrect user`() {

    val correctUser = User("testUser", "password", emptySet())
    val incorrectUser = User("wrongUser", "password", emptySet())
    val token = jwtTokenUtil.generateToken(correctUser)

    assertThat(jwtTokenUtil.validateToken(token, incorrectUser)).isFalse()
  }

  @Test
  fun `should allow tokens within clock skew period`() {
    val expTime = System.currentTimeMillis() - 20_000
    val user = User("test", "pwd", emptySet())

    val token = Jwts.builder()
      .setSubject(user.username)
      .setIssuedAt(Date())
      .setExpiration(Date(expTime))
      .signWith(jwtTokenUtil.getKeyForTesting(), SignatureAlgorithm.HS512)
      .compact()

    val util = JwtTokenUtil(secret, 3600, 30)
    assertThat(util.validateToken(token, user)).isTrue()
  }

  @Test
  fun `should detect expired token`() {
    val userDetails = User("testUser", "password", emptySet())
    val expTime = System.currentTimeMillis() - 60_000
    val token = Jwts.builder()
      .setSubject(userDetails.username)
      .setIssuedAt(Date())
      .setExpiration(Date(expTime))
      .signWith(jwtTokenUtil.getKeyForTesting(), SignatureAlgorithm.HS512)
      .compact()


    val util = JwtTokenUtil(secret, 3600, 30)
    assertThat(util.validateToken(token, userDetails)).isFalse()
  }

  @Test
  fun `should include roles in token claims`() {
    val authorities = setOf("ROLE_USER", "ROLE_ADMIN")
    val userDetails = User("testUser", "password", authorities)
    val token = jwtTokenUtil.generateToken(userDetails)
    val claims = Jwts.parserBuilder()
      .setSigningKey(jwtTokenUtil.getKeyForTesting())
      .build()
      .parseClaimsJws(token)
      .body

    val roles = claims["roles"] as List<*>
    assertThat(roles).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN")
  }

  @Test
  fun `should handle invalid token gracefully`() {
    val user = User("testUser", "pwd", emptySet())
    val validToken = jwtTokenUtil.generateToken(user)
    val parts = validToken.split('.')
    val tamperedToken = "${parts[0]}.${parts[1]}.invalidSignature"

    assertThat(jwtTokenUtil.getUsernameFromToken(tamperedToken)).isNull()
  }

  private fun JwtTokenUtil.getKeyForTesting(): SecretKey = this.javaClass
    .getDeclaredField("key")
    .apply { isAccessible = true }
    .get(this) as SecretKey
}
