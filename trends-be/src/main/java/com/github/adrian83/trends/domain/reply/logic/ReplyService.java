package com.github.adrian83.trends.domain.reply.logic;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.github.adrian83.trends.domain.reply.model.ReplyMapper;
import com.github.adrian83.trends.domain.reply.storage.ReplyRepository;
import com.github.adrian83.trends.domain.status.StatusSource;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class ReplyService implements Service<Reply> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReplyService.class);

  @Autowired private ReplyRepository replyRepository;
  @Autowired private StatusSource twittsSource;
  @Autowired private ReplyMapper replyMapper;

  private ConnectableFlux<List<Reply>> replies;

  @PostConstruct
  public void postCreate() {
    LOGGER.info("Created");
    persistReplies();
    readReplies();
    LOGGER.info("Reading and persisting replies initiated");
  }

  @Override
  public Flux<List<Reply>> top() {
    return replies;
  }

  @Override
  @Scheduled(
	      fixedDelayString = "${reply.cleaning.fixedRateMs}",
	      initialDelayString = "${reply.cleaning.initialDelayMs}")
  public void removeUnused() {
    Mono<DeleteResult> result = replyRepository.deleteOlderThan(1, TimeUnit.MINUTES);
    result.subscribe(REMOVE_SUCCESS_CONSUMER, REMOVE_ERROR_CONSUMER);
  }

  private void persistReplies() {
    LOGGER.info("Starting persisting replies");
    twittsSource
        .twittsFlux()
        .flatMap(this::toReply)
        .map(replyRepository::save)
        .subscribe(PERSIST_SUCCESS_CONSUMER, PERSIST_ERROR_CONSUMER);
  }

  private Mono<ReplyDoc> toReply(Status status) {

    if (status.getInReplyToStatusId() < 0 || status.getInReplyToScreenName() == null) {
      return Mono.empty();
    }
    ReplyDoc doc =
        new ReplyDoc(
            null,
            status.getInReplyToStatusId(),
            status.getInReplyToScreenName(),
            1l,
            Time.utcNow());
    return Mono.just(doc);
  }

  private void readReplies() {
    LOGGER.info("Reading most replied twitts");
    replies =
        Flux.interval(Duration.ofSeconds(10))
            .flatMap(i -> replyRepository.top(10))
            .map(list -> list.stream().map(replyMapper::docToDto).collect(Collectors.toList()))
            .publish();
    replies.connect();
  }

  private static final Consumer<Mono<UpdateResult>> PERSIST_SUCCESS_CONSUMER =
      (Mono<UpdateResult> updateResult) ->
          updateResult.subscribe(ur -> LOGGER.info("Reply updated: {}", ur));

  private static final Consumer<Throwable> PERSIST_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during processing replies {}", fault);

  private static final Consumer<DeleteResult> REMOVE_SUCCESS_CONSUMER =
      (DeleteResult deleteResult) -> LOGGER.warn("Replies removed: {}", deleteResult);

  private static final Consumer<Throwable> REMOVE_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during removing replies {}", fault);
}
