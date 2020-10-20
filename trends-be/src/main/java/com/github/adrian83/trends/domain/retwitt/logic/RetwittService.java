package com.github.adrian83.trends.domain.retwitt.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.justOrEmpty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.common.logging.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.domain.retwitt.model.RetwittDoc;
import com.github.adrian83.trends.domain.retwitt.model.RetwittMapper;
import com.github.adrian83.trends.domain.retwitt.storage.RetwittRepository;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Service
public class RetwittService implements StatusProcessor, StatusCleaner, StatusFetcher<Retwitt> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetwittService.class);

  private RetwittRepository retwittRepository;
  private RetwittMapper retwittMapper;

  @Autowired
  public RetwittService(RetwittRepository retwittRepository, RetwittMapper retwittMapper) {
    super();
    this.retwittRepository = retwittRepository;
    this.retwittMapper = retwittMapper;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting retwitts initiated");
    statusses
        .flatMap(this::toRetwittDoc)
        .map(retwittRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<>(Retwitt.class),
            new DocPersistingErrorHandler<>(Retwitt.class));
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    retwittRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<>(Retwitt.class),
            new DocRemovingErrorHandler<>(Retwitt.class));
  }

  @Override
  public Flux<List<Retwitt>> fetch(int size, int seconds) {
    LOGGER.info("Reading most retwitted twitts");
    ConnectableFlux<List<Retwitt>> retwitted =
        Flux.interval(ofSeconds(seconds))
            .flatMap(i -> retwittRepository.top(size))
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
