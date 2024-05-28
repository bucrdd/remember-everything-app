package com.niuma.remembereverything.config;

import static java.util.Collections.singletonList;

import com.niuma.remembereverything.repository.UserRepository;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
        .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/auth/signin", "/auth/signup").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
        )
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)) // for h2-console
        .build();
  }

  /**
   * Cors配置优化
   **/
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    org.springframework.web.cors.CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(singletonList("*"));
    // configuration.setAllowedOriginPatterns(singletonList("*"));
    configuration.setAllowedHeaders(singletonList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
    configuration.setAllowCredentials(false);
    configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }



  @Bean
  UserDetailsService userDetailsService(UserRepository users) {
    return username -> users.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username:" + username + " not found"));
  }

  @Bean
  AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    return authentication -> {
      String username = authentication.getPrincipal() + "";
      UserDetails user = userDetailsService.loadUserByUsername(username);
      if (!passwordEncoder.matches(username, user.getPassword())) {
        throw new BadCredentialsException("Bad credentials");
      }

      if (!user.isEnabled()) {
        throw new DisabledException("Disabled");
      }

      return new UsernamePasswordAuthenticationToken(username, user.getPassword(), user.getAuthorities());
    };
  }

}
