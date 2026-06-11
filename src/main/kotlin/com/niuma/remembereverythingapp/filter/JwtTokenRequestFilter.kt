package com.niuma.remembereverythingapp.filter

import com.niuma.remembereverythingapp.util.JwtTokenUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtTokenRequestFilter(
  private val jwtTokenUtil: JwtTokenUtil,
  private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain) {

    val authHeader = request.getHeader("Authorization")
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      val token = authHeader.substring(7)
      val username = jwtTokenUtil.getUsernameFromToken(token)
      if (username != null && SecurityContextHolder.getContext().authentication == null) {
        val userDetails = userDetailsService.loadUserByUsername(username)

        if (jwtTokenUtil.validateToken(token, userDetails)) {
          val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
          authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
          SecurityContextHolder.getContext().authentication = authentication
        } else {
          logger.info("Token invalid or expired for username[$username]")
          return
        }
      } else {
        logger.info("Username from token is valid or request already authenticated")
      }
    } else {
      logger.info("""Not found "Authorization" from Header or Authorization no starting with "Bearer".""")
      return
    }
    filterChain.doFilter(request, response)
  }
}