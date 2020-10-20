package com.github.adrian83.trends.domain.retwitt.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.web.BaseController;

import reactor.core.publisher.Flux;

@RestController
public class RetwittController extends BaseController<Retwitt> {

  public static final String RETWEETS = "retwitts";

  @Value("${read.intervalSec}")
  private int readIntervalSec;

  @Value("${read.count}")
  private int readCount;

  @Autowired private StatusFetcher<Retwitt> retwittService;

  @GetMapping(value = SSE_PATH + RETWEETS, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Retwitt>>> sseRetwitted() {
    return toSse(retwittService.fetch(readCount, readIntervalSec));
  }
}
