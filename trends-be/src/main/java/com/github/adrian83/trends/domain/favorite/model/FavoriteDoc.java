package com.github.adrian83.trends.domain.favorite.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;

@JsonSerialize
@JsonDeserialize
@Document(collection = FavoriteDoc.COLLECTION)
@Builder
public class FavoriteDoc {

  public static final String COLLECTION = "favorites";

  public static final String ID = "id";
  public static final String USERNAME = "username";
  public static final String UPDATED = "updated";
  public static final String COUNT = "count";
 
  @Id
  @Field(ID)
  private String id;

  @Field(USERNAME)
  private String username;

  @Field(COUNT)
  private long count;

  @Field(UPDATED)
  private Long updated;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    return "FavoriteDoc [username=" + username + ", count=" + count + ", updated=" + updated + "]";
  }
}
