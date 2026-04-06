package com.kapotiabi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class KapotiabiApplication {

  public static void main(String[] args) {
    SpringApplication.run(KapotiabiApplication.class, args);
  }
}
