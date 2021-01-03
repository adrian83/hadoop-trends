package com.github.adrian83.trends.domain.hashtag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonSerialize
@JsonDeserialize
@Document(collection = HashtagDoc.COLLECTION)
@Builder
@Data
@ToString
@EqualsAndHashCode
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
}
