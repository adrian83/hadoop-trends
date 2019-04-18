package com.github.adrian83.trends.domain.favorite;

public class Favorite {

  private String twittId;
  private String username;
  private long count;

  public Favorite() {
    super();
  }

  public Favorite(String twittId, String username, long count) {
    this();
    this.twittId = twittId;
    this.username = username;
    this.count = count;
  }

  public String getTwittId() {
    return twittId;
  }

  public void setTwittId(String twittId) {
    this.twittId = twittId;
  }

  public String getUsername() {
    return username;
  }

  public long getCount() {
    return count;
  }
}
