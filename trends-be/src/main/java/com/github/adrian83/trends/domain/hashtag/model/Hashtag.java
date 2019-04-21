package com.github.adrian83.trends.domain.hashtag.model;

public class Hashtag {

  private String name;
  private long count;

  public Hashtag() {
    super();
  }

  public Hashtag(String name, long count) {
    this();
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCount() {
    return count;
  }

  public void setCount(long count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "Hashtag [name=" + name + ", count=" + count + "]";
  }
}
