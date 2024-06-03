package com.niuma.remembereverything.controller;


import com.niuma.remembereverything.exception.BusinessException;
import com.niuma.remembereverything.repository.UserRepository;
import com.niuma.remembereverything.web.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

}
