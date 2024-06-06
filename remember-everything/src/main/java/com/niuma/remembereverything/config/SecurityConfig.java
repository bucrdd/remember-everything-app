package com.niuma.remembereverything.config;

import static java.util.Collections.singletonList;

import com.niuma.remembereverything.repository.UserRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationEntryPoint authenticationEntryPoint;

  private final AccessDeniedHandler accessDeniedHandler;

  @Bean
  SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.httpBasic(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)) // for h2-console
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/auth/login", "/auth/signup").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/management/**").hasRole("MANAGER")
            .anyRequest().authenticated()
        )
        .logout(logout -> logout
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
        )
        .sessionManagement(session -> session
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())
        )
        .securityContext(context -> context.requireExplicitSave(true))
        .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .exceptionHandling(handling -> handling
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .build();
  }

  /**
   * Cors配置优化
   **/
//  @Bean
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
      String password = authentication.getCredentials() + "";
      UserDetails user = userDetailsService.loadUserByUsername(username);
      if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new BadCredentialsException("Bad credentials");
      }

      if (!user.isEnabled()) {
        throw new DisabledException("Disabled");
      }

      return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    };
  }

  @Bean
  SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }

}
