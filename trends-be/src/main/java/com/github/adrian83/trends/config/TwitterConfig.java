package com.github.adrian83.trends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitterConfig {

  @Value("${twitter.consumerKey}")
  private String customerKey;

  @Value("${twitter.consumerSecret}")
  private String customerSecret;

  @Value("${twitter.token}")
  private String token;

  @Value("${twitter.secret}")
  private String secret;

  public String getCustomerKey() {
    return customerKey;
  }

  public String getCustomerSecret() {
    return customerSecret;
  }

  public String getToken() {
    return token;
  }

  public String getSecret() {
    return secret;
  }
}
