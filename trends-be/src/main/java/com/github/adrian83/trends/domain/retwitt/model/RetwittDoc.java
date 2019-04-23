package com.github.adrian83.trends.domain.retwitt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = RetwittDoc.COLLECTION)
public class RetwittDoc {

  public static final String COLLECTION = "retwitts";

  public static final String ID = "id";
  public static final String TWITT_ID = "twitt_id";
  public static final String USERNAME = "username";
  public static final String UPDATED = "updated";
  public static final String RETWITT_COUNT = "count";

  @Id
  @Field(ID)
  private String id;

  @Field(TWITT_ID)
  private Long twittId;

  @Field(USERNAME)
  private String username;

  @Field(RETWITT_COUNT)
  private long count;

  @Field(UPDATED)
  private Long updated;

  public RetwittDoc() {
    super();
  }

  public RetwittDoc(Long twittId, String username, long count, Long updated) {
    this();
    this.id = String.valueOf(twittId);
    this.twittId = twittId;
    this.username = username;
    this.count = count;
    this.updated = updated;
  }

  public String getId() {
    return id;
  }

  public Long getTwittId() {
    return twittId;
  }

  public void setTwittId(Long twittId) {
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

  public Long getUpdated() {
    return updated;
  }

  public void setUpdated(Long updated) {
    this.updated = updated;
  }

  @Override
  public String toString() {
    return "RetwittDoc [id="
        + id
        + ", twittId="
        + twittId
        + ", username="
        + username
        + ", count="
        + count
        + ", updated="
        + updated
        + "]";
  }
}
