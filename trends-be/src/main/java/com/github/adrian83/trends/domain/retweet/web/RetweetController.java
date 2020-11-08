package com.github.adrian83.trends.domain.retweet.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.retweet.model.Retweet;
import com.github.adrian83.trends.web.BaseController;

import reactor.core.publisher.Flux;

@RestController
public class RetweetController extends BaseController<Retweet> {

  public static final String RETWEETS = "retweets";

  @Value("${read.intervalSec}")
  private int readIntervalSec;

  @Value("${read.count}")
  private int readCount;

  @Autowired private StatusFetcher<Retweet> retweetService;

  @GetMapping(value = SSE_PATH + RETWEETS, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Retweet>>> sseRetweeted() {
    return toSse(retweetService.fetch(readCount, readIntervalSec));
  }
}
