package com.github.adrian83.trends.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    corsRegistry.addMapping("/**");
    // .allowedOrigins("*")
    // .allowedMethods("PUT")
    // .maxAge(3600);
  }
}
