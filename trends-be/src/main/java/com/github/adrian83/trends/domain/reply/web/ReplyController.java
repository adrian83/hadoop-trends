package com.github.adrian83.trends.domain.reply.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.favorite.web.FavoriteController;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.web.BaseController;

import reactor.core.publisher.Flux;

@RestController
public class ReplyController extends BaseController<Reply> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);

  public static final String REPLIES = "replies";
  
  @Value("${read.intervalSec}") private int readIntervalSec;
  @Value("${read.count}") private int readCount;

  @Autowired private StatusFetcher<Reply> replyService;

  @GetMapping(value = SSE_PATH + REPLIES, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Reply>>> sseReplies() {
    LOGGER.warn("Getting most replied twitts");
    return toSse(replyService.fetch(readCount, readIntervalSec));
  }

  @RequestMapping(value = "/view/" + REPLIES)
  public String replies() {
    return REPLIES;
  }
}
