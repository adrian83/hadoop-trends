package com.github.adrian83.trends.domain.hashtag.model;

import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;

@Component	
public class HashtagMapper {

  public HashtagDoc dtoToDoc(Hashtag hashtag) {
    return HashtagDoc.builder()
        .id(hashtag.getName())
        .count(hashtag.getCount())
        .updated(Time.utcNow())
        .build();
  }

  public Hashtag docToDto(HashtagDoc hashtagDoc) {
    return Hashtag.builder().name(hashtagDoc.getName()).count(hashtagDoc.getCount()).build();
  }
}
