package com.github.adrian83.trends.domain.reply.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.common.Service;
import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.reply.model.Reply;
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.github.adrian83.trends.domain.reply.model.ReplyMapper;
import com.github.adrian83.trends.domain.reply.storage.ReplyRepository;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class ReplyService implements Service<Reply>, StatusProcessor, StatusCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReplyService.class);

  private static final Consumer<Throwable> DOC_REMOVING_ERROR_HANDLER =
      new DocRemovingErrorHandler<>(Reply.class);
  private static final Consumer<Long> DOC_REMOVING_SUCCESS_HANDLER =
      new DocRemovingSuccessHandler<>(Reply.class);
  private static final Consumer<Throwable> DOC_PERSISTING_ERROR_HANDLER =
      new DocPersistingErrorHandler<>(Reply.class);
  private static final Consumer<Mono<String>> DOC_PERSISTING_SUCCESS_HANDLER =
      new DocPersistingSuccessHandler<>(Reply.class);

  @Autowired private ReplyRepository replyRepository;
  @Autowired private ReplyMapper replyMapper;

  @Value("${reply.read.intervalSec}")
  private int readIntervalSec;

  @Value("${reply.read.count}")
  private int readCount;

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting replies initiated");
    statusses
        .flatMap(this::toReply)
        .map(replyRepository::save)
        .subscribe(DOC_PERSISTING_SUCCESS_HANDLER, DOC_PERSISTING_ERROR_HANDLER);
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    replyRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(DOC_REMOVING_SUCCESS_HANDLER, DOC_REMOVING_ERROR_HANDLER);
  }

  @Override
  public Flux<List<Reply>> top() {
    LOGGER.info("Reading most replied twitts");
    ConnectableFlux<List<Reply>> replies =
        Flux.interval(ofSeconds(readIntervalSec))
            .flatMap(i -> replyRepository.top(readCount))
            .map(this::toDtos)
            .publish();
    replies.connect();
    return replies;
  }

  private Mono<ReplyDoc> toReply(Status status) {
    return Mono.justOrEmpty(status)
        .filter(s -> s.getInReplyToStatusId() >= 0)
        .filter(s -> nonNull(s.getInReplyToScreenName()))
        .map(s -> new ReplyDoc(s.getInReplyToStatusId(), s.getInReplyToScreenName(), 1l, utcNow()));
  }

  private List<Reply> toDtos(List<ReplyDoc> docs) {
    return docs.stream().map(replyMapper::docToDto).collect(Collectors.toList());
  }
}
