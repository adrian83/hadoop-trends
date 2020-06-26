package com.github.adrian83.trends.domain.reply.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.just;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.DocRemovingSuccessHandler;
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

  @PostConstruct
  public void postCreate() {
    persistReplies();
    LOGGER.info("Persisting replies initiated");
  }

  @Override
  public Flux<List<Reply>> top() {
    LOGGER.info("Reading most replied twitts");
    ConnectableFlux<List<Reply>> replies =
        Flux.interval(Duration.ofSeconds(readIntervalSec))
            .flatMap(i -> replyRepository.top(readCount))
            .map(this::toDtos)
            .publish();
    replies.connect();
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
            new DocRemovingSuccessHandler<Reply>(Reply.class),
            new DocRemovingErrorHandler<Reply>(Reply.class));
  }

  private void persistReplies() {
    LOGGER.info("Starting persisting replies");
    twittsSource
        .twittsFlux()
        .flatMap(this::toReply)
        .map(replyRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<Reply>(Reply.class),
            new DocPersistingErrorHandler<Reply>(Reply.class));
  }

  private Mono<ReplyDoc> toReply(Status status) {
    return just(status)
        .filter(s -> s != null)
        .filter(s -> s.getInReplyToStatusId() >= 0)
        .filter(s -> s.getInReplyToScreenName() != null)
        .map(this::toDoc);
  }

  private List<Reply> toDtos(List<ReplyDoc> docs) {
    return docs.stream().map(replyMapper::docToDto).collect(toList());
  }

  private ReplyDoc toDoc(Status status) {
    return new ReplyDoc(
        status.getInReplyToStatusId(), status.getInReplyToScreenName(), 1l, utcNow());
  }
}
