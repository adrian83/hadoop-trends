package com.github.adrian83.trends.domain.reply.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static reactor.core.publisher.Mono.empty;
import static reactor.core.publisher.Mono.just;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Service;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.github.adrian83.trends.domain.reply.model.ReplyMapper;
import com.github.adrian83.trends.domain.reply.storage.ReplyRepository;
import com.github.adrian83.trends.domain.status.StatusSource;

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

  @Value("${reply.read.intervalSec}")
  private int readIntervalSec;

  @Value("${reply.read.count}")
  private int readCount;

  @Value("${reply.cleaning.olderThanSec}")
  private int olderThanSec;

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
    replyRepository
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(
            createRemoveSuccessConsumer(Reply.class, LOGGER),
            createRemoveErrorConsumer(Reply.class, LOGGER));
  }

  private void persistReplies() {
    LOGGER.info("Starting persisting replies");
    twittsSource
        .twittsFlux()
        .flatMap(this::toReply)
        .map(replyRepository::save)
        .subscribe(
            createPersistSuccessConsumer(Reply.class, LOGGER),
            createPersistErrorConsumer(Reply.class, LOGGER));
  }

  private Mono<ReplyDoc> toReply(Status status) {
    if (status.getInReplyToStatusId() < 0 || status.getInReplyToScreenName() == null) {
      return empty();
    }
    
    var doc =
        new ReplyDoc(status.getInReplyToStatusId(), status.getInReplyToScreenName(), 1l, utcNow());
    return just(doc);
  }

  private void readReplies() {
    LOGGER.info("Reading most replied twitts");
    replies =
        Flux.interval(Duration.ofSeconds(readIntervalSec))
            .flatMap(i -> replyRepository.top(readCount))
            .map(list -> list.stream().map(replyMapper::docToDto).collect(Collectors.toList()))
            .publish();
    replies.connect();
  }
}
