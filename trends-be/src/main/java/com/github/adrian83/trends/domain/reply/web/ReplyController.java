package com.github.adrian83.trends.domain.reply.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.web.BaseController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
public class ReplyController extends BaseController<Reply> {

  public static final String REPLIES = "replies";

  @Value("${read.intervalSec}")
  private int readIntervalSec;

  @Value("${read.count}")
  private int readCount;

  @Autowired private StatusFetcher<Reply> replyService;

  @GetMapping(value = SSE_PATH + REPLIES, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Reply>>> sseReplies() {
    log.info("Getting most replied tweets");
    return toSse(replyService.fetch(readCount, readIntervalSec));
  }
}
