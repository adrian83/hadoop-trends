package com.github.adrian83.trends.domain.retwitt.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.justOrEmpty;

import java.util.List;
import java.util.function.Consumer;

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
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.domain.retwitt.model.RetwittDoc;
import com.github.adrian83.trends.domain.retwitt.model.RetwittMapper;
import com.github.adrian83.trends.domain.retwitt.storage.RetwittRepository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class RetwittService implements Service<Retwitt>, StatusProcessor, StatusCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetwittService.class);

  private static final Consumer<Throwable> DOC_REMOVING_ERROR_HANDLER =
      new DocRemovingErrorHandler<>(Retwitt.class);
  private static final Consumer<Long> DOC_REMOVING_SUCCESS_HANDLER =
      new DocRemovingSuccessHandler<>(Retwitt.class);
  private static final Consumer<Throwable> DOC_PERSISTING_ERROR_HANDLER =
      new DocPersistingErrorHandler<>(Retwitt.class);
  private static final Consumer<Mono<String>> DOC_PERSISTING_SUCCESS_HANDLER =
      new DocPersistingSuccessHandler<>(Retwitt.class);

  @Autowired private RetwittRepository retwittRepository;
  @Autowired private RetwittMapper retwittMapper;

  @Value("${retwitt.read.intervalSec}")
  private int readIntervalSec;

  @Value("${retwitt.read.count}")
  private int readCount;

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting retwitts initiated");
    statusses
        .flatMap(this::toRetwittDoc)
        .map(retwittRepository::save)
        .subscribe(DOC_PERSISTING_SUCCESS_HANDLER, DOC_PERSISTING_ERROR_HANDLER);
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    retwittRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(DOC_REMOVING_SUCCESS_HANDLER, DOC_REMOVING_ERROR_HANDLER);
  }

  @Override
  public Flux<List<Retwitt>> top() {
    LOGGER.info("Reading most retwitted twitts");
    ConnectableFlux<List<Retwitt>> retwitted =
        Flux.interval(ofSeconds(readIntervalSec))
            .flatMap(i -> retwittRepository.top(readCount))
            .map(this::toDtos)
            .publish();
    retwitted.connect();
    return retwitted;
  }

  private Mono<RetwittDoc> toRetwittDoc(Status status) {
    return justOrEmpty(status)
        .filter(s -> nonNull(s.getRetweetedStatus()))
        .map(Status::getRetweetedStatus)
        .filter(s -> nonNull(s.getUser()))
        .filter(s -> nonNull(s.getUser().getScreenName()))
        .map(this::toDoc);
  }

  private RetwittDoc toDoc(Status s) {
    return new RetwittDoc(s.getId(), s.getUser().getScreenName(), s.getRetweetCount(), utcNow());
  }

  private List<Retwitt> toDtos(List<RetwittDoc> docs) {
    return docs.stream().map(retwittMapper::docToDto).collect(toList());
  }
}
