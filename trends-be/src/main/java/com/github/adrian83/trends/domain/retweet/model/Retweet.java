package com.github.adrian83.trends.domain.retweet.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class Retweet {
  private String tweetId;
  private String username;
  private long count;
}
