package com.github.adrian83.trends.domain.favorite;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = FavoriteDoc.COLLECTION)
public class FavoriteDoc {

  public static final String COLLECTION = "favorites";

  public static final String ID = "id";
  public static final String TWITT_ID = "twitt_id";
  public static final String USERNAME = "username";
  public static final String UPDATED = "updated";
  public static final String COUNT = "count";

  @Id
  @Field(ID)
  private String documentId;

  @Field(TWITT_ID)
  private Long twittId;

  @Field(USERNAME)
  private String username;

  @Field(COUNT)
  private Long count;

  @Field(UPDATED)
  private Long updated;

  public FavoriteDoc() {
    super();
  }

  public FavoriteDoc(String documentId, Long twittId, String username, Long count, Long updated) {
    this();
    this.documentId = documentId;
    this.twittId = twittId;
    this.username = username;
    this.count = count;
    this.updated = updated;
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

  public Long getCount() {
    return count;
  }

  public Long getUpdated() {
    return updated;
  }
}
