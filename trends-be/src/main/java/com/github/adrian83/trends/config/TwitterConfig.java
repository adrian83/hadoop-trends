package com.github.adrian83.trends.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
//@PropertySource("classpath:config/twitter.properties")
public class TwitterConfig {

  // private static final Logger LOGGER = LoggerFactory.getLogger(TwitterConfig.class);

  // private static final String CUSTOMER_KEY = "consumer.key";
  // private static final String CUSTOMER_SECRET = "consumer.secret";
  // private static final String TOKEN = "token";
  // private static final String SECRET = "secret";

  @Value("${twitter.consumer.key}")
  private String customerKey;

  @Value("${twitter.consumer.secret}")
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
