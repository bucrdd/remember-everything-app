package com.niuma.remembereverything.controller;


import com.niuma.remembereverything.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

  private final SecurityContextRepository securityContextRepository = new DelegatingSecurityContextRepository(
      new HttpSessionSecurityContextRepository(), new RequestAttributeSecurityContextRepository());

  private final AuthenticationManager authenticationManager;


  @PostMapping("/login")
  public String signIn(@RequestBody AuthenticationRequest data, HttpServletRequest request, HttpServletResponse response) {
    try {
      String username = data.getUsername();
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, data.getPassword()));
      SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
      context.setAuthentication(authentication);
      this.securityContextHolderStrategy.setContext(context);
      this.securityContextRepository.saveContext(context, request, response);
      return username + " signed in.";
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  @GetMapping("/me")
  public User me(@AuthenticationPrincipal(errorOnInvalidType = true) UserDetails userDetails) {
    if (userDetails instanceof User user) {
      log.debug("user {} checked his/her personal information", user.getUsername());
      return user;
    } else {
      throw new ClassCastException(userDetails.getClass().getName() + " cannot cast to " + User.class.getName());
    }
  }

}
