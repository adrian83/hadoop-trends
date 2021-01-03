package com.github.adrian83.trends.domain.reply.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.common.logging.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.github.adrian83.trends.domain.reply.model.ReplyMapper;
import com.github.adrian83.trends.domain.reply.storage.ReplyRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Slf4j
@Component
public class ReplyService implements StatusProcessor, StatusCleaner, StatusFetcher<Reply> {

  private ReplyRepository replyRepository;
  private ReplyMapper replyMapper;

  @Autowired
  public ReplyService(ReplyRepository replyRepository, ReplyMapper replyMapper) {
    super();
    this.replyRepository = replyRepository;
    this.replyMapper = replyMapper;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    log.info("Persisting replies initiated");
    statusses
        .flatMap(this::toReply)
        .map(replyRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<>(Reply.class),
            new DocPersistingErrorHandler<>(Reply.class));
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    replyRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<>(Reply.class),
            new DocRemovingErrorHandler<>(Reply.class));
  }

  @Override
  public Flux<List<Reply>> fetch(int size, int seconds) {
    log.info("Reading most replied twitts");
    ConnectableFlux<List<Reply>> replies =
        Flux.interval(ofSeconds(seconds))
            .flatMap(i -> replyRepository.top(size))
            .map(this::toDtos)
            .publish();
    replies.connect();
    return replies;
  }

  private Mono<ReplyDoc> toReply(Status status) {
    return Mono.justOrEmpty(status)
        .filter(s -> s.getInReplyToStatusId() >= 0)
        .filter(s -> nonNull(s.getInReplyToScreenName()))
        .map(this::toDoc);
  }

  private ReplyDoc toDoc(Status s) {
    var tweetId = Long.toString(s.getInReplyToStatusId());
    return ReplyDoc.builder()
        .id(tweetId)
        .tweetId(tweetId)
        .username(s.getInReplyToScreenName())
        .count(1l)
        .updated(utcNow())
        .build();
  }

  private List<Reply> toDtos(List<ReplyDoc> docs) {
    return docs.stream().map(replyMapper::docToDto).collect(Collectors.toList());
  }
}
