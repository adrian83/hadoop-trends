package com.github.adrian83.trends.domain.favorite.model;

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

  public void setUsername(String username) {
    this.username = username;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "Favorite [twittId=" + twittId + ", username=" + username + ", count=" + count + "]";
  }
}
