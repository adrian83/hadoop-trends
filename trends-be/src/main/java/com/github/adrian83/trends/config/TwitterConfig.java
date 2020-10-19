package com.github.adrian83.trends.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

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

  @Bean
  public TwitterStream createTwitterStream() {

    var twitterStream = new TwitterStreamFactory().getInstance();

    twitterStream.setOAuthConsumer(customerKey, customerSecret);
    twitterStream.setOAuthAccessToken(new AccessToken(token, secret));

    twitterStream.addListener(
        new StatusAdapter() {
          @Override
          public void onStatus(Status status) {}

          @Override
          public void onException(Exception ex) {}
        });

    twitterStream.sample();

    return twitterStream;
  }
}
