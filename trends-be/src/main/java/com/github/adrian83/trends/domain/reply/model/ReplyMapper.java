package com.github.adrian83.trends.domain.reply.model;

import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;

@Component
public class ReplyMapper {

  public ReplyDoc dtoToDoc(Reply reply) {
    return ReplyDoc.builder()
        .id(reply.getTweetId())
        .tweetId(reply.getTweetId())
        .username(reply.getUsername())
        .count(reply.getCount())
        .updated(Time.utcNow())
        .build();
  }

  public Reply docToDto(ReplyDoc replyDoc) {
    return Reply.builder()
        .tweetId(replyDoc.getTweetId())
        .username(replyDoc.getUsername())
        .count(replyDoc.getCount())
        .build();
  }
}
