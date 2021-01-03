package com.github.adrian83.trends.domain.reply.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class Reply {
  private String tweetId;
  private String username;
  private long count;
}
