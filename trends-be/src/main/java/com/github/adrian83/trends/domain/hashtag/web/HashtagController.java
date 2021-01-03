package com.github.adrian83.trends.domain.hashtag.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.hashtag.model.Hashtag;
import com.github.adrian83.trends.web.BaseController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class HashtagController extends BaseController<Hashtag> {

  private static final String HASHTAGS = "hashtags";

  @Value("${read.intervalSec}")
  private int readIntervalSec;

  @Value("${read.count}")
  private int readCount;

  @Autowired private StatusFetcher<Hashtag> hashtagService;

  @GetMapping(value = SSE_PATH + HASHTAGS, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Hashtag>>> sseHashtags() {
    log.info("Getting most popular hashtags");
    return toSse(hashtagService.fetch(readCount, readIntervalSec));
  }
}
