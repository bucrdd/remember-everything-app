package com.niuma.remembereverything;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class RememberEverythingApplication {

  public static void main(String[] args) {
    SpringApplication.run(RememberEverythingApplication.class, args);
  }

}
