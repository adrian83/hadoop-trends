package com.github.adrian83.trends.domain.hashtag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Document(collection = HashtagDoc.COLLECTION)
public class HashtagDoc {

  public static final String COLLECTION = "hashtags";

  public static final String ID = "id";
  public static final String UPDATED = "updated";
  public static final String NAME = "name";
  public static final String COUNT = "count";

  @Id
  @Field(ID)
  private String id;

  @Field(NAME)
  private String name;

  @Field(COUNT)
  private long count;

  @Field(UPDATED)
  private Long updated;

  public HashtagDoc() {
    super();
  }

  public HashtagDoc(String name, long count, Long updated) {
    this();
    this.id = name;
    this.name = name;
    this.count = count;
    this.updated = updated;
  }

  public String getId() {
    return id;
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

  public Long getUpdated() {
    return updated;
  }

  public void setUpdated(Long updated) {
    this.updated = updated;
  }

  @Override
  public String toString() {
    return "HashtagDoc [id="
        + id
        + ", name="
        + name
        + ", count="
        + count
        + ", updated="
        + updated
        + "]";
  }
}
