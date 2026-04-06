package com.kapotiabi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI kapotiabiOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Kapotiabi API")
                .description(
                    "Car repair marketplace — connects car owners with repair shops in Estonia. "
                        + "Car owners submit job requests, shops submit offers, owners book and review.")
                .version("v1")
                .contact(new Contact().name("Team 23 — TalTech")));
  }
}
