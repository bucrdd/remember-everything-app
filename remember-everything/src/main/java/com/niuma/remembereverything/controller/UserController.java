package com.niuma.remembereverything.controller;

import com.niuma.remembereverything.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/management/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final SessionRegistry sessionRegistry;

  @GetMapping("/online-list")
  public List<User> users() {
    List<Object> principals = sessionRegistry.getAllPrincipals();
    log.info("online user`s amount = {}", principals.size());
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    System.out.println(principal);
    return principals.stream()
        .map(User.class::cast)
        .toList();
  }


}
