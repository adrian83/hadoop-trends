package com.github.adrian83.trends.domain.hashtag.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@ToString
@EqualsAndHashCode
public class Hashtag {
  private String name;
  private long count;
}
