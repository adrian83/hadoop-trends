package com.github.adrian83.trends.domain.retweet.model;

import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;

@Component
public class RetweetMapper {

  public RetweetDoc dtoToDoc(Retweet retweet) {
    return RetweetDoc.builder()
        .id(retweet.getTweetId())
        .tweetId(retweet.getTweetId())
        .username(retweet.getUsername())
        .count(retweet.getCount())
        .updated(Time.utcNow())
        .build();
  }

  public Retweet docToDto(RetweetDoc retweetDoc) {
    return Retweet.builder()
        .tweetId(retweetDoc.getTweetId())
        .count(retweetDoc.getCount())
        .username(retweetDoc.getUsername())
        .build();
  }
}
