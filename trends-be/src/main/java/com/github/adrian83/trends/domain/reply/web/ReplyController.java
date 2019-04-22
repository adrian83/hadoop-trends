package com.github.adrian83.trends.domain.reply.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adrian83.trends.common.web.SseController;
import com.github.adrian83.trends.domain.favorite.web.FavoriteController;
import com.github.adrian83.trends.domain.reply.logic.ReplyService;
import com.github.adrian83.trends.domain.reply.model.Reply;

import reactor.core.publisher.Flux;

import static com.github.adrian83.trends.common.web.ViewController.SSE_CONTENT_TYPE;
import static com.github.adrian83.trends.common.web.ViewController.SSE_PATH;

@RestController
public class ReplyController extends SseController<Reply> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);

  public static final String REPLIES = "replies";

  @Autowired private ReplyService replyService;

  @GetMapping(value = SSE_PATH + REPLIES, produces = SSE_CONTENT_TYPE)
  public Flux<ServerSentEvent<List<Reply>>> sseReplies() {
    LOGGER.warn("Getting most replied twitts");
    return toSse(replyService.top());
  }

  @RequestMapping(value = "/view/" + REPLIES)
  public String replies() {
    return REPLIES;
  }
}
