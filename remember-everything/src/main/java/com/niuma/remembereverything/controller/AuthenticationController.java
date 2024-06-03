package com.niuma.remembereverything.controller;


import com.niuma.remembereverything.entity.User;
import com.niuma.remembereverything.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final SessionRegistry sessionRegistry;

  @PostMapping("/signin")
  public String signIn(@RequestBody AuthenticationRequest data) {
    try {

      String username = data.getUsername();
      var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
      return "signed";
    } catch (AuthenticationException e) {
      throw new BadCredentialsException("Invalid username or password");
    }
  }

  @GetMapping("/management/users")
  public List<User> users() {
    sessionRegistry.getAllPrincipals();
    return Collections.EMPTY_LIST;
  }

}
