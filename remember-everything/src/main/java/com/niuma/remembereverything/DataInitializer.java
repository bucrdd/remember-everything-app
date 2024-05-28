package com.niuma.remembereverything;

import com.niuma.remembereverything.entity.User;
import com.niuma.remembereverything.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository users;

  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    log.debug("Initializing user data...");
    this.users.save(User.builder()
        .username("user")
        .password(passwordEncoder.encode("password"))
        .roles(List.of("ROLE_USER"))
        .build());
    this.users.save(User.builder()
        .username("admin")
        .password(passwordEncoder.encode("password"))
        .roles(List.of("ROLE_USER", "ROLE_ADMIN"))
        .build());
    log.debug("Printing all users...");
    this.users.findAll().forEach(u -> log.debug("User: {}", u.toString()));
  }
}
