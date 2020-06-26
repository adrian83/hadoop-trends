package com.github.adrian83.trends.domain.retwitt.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.just;

import java.util.List;
import java.util.Objects;
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
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.domain.retwitt.model.RetwittDoc;
import com.github.adrian83.trends.domain.retwitt.model.RetwittMapper;
import com.github.adrian83.trends.domain.retwitt.storage.RetwittRepository;
import com.github.adrian83.trends.domain.status.StatusSource;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class RetwittService implements Service<Retwitt> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetwittService.class);

  @Autowired private RetwittRepository retwittRepository;
  @Autowired private StatusSource twittsSource;
  @Autowired private RetwittMapper retwittMapper;

  @Value("${retwitt.read.intervalSec}")
  private int readIntervalSec;

  @Value("${retwitt.read.count}")
  private int readCount;

  @Value("${retwitt.cleaning.olderThanSec}")
  private int olderThanSec;

  @PostConstruct
  public void postCreate() {
    persistRetwitts();
    LOGGER.info("Persisting retwitts initiated");
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

  @Override
  @Scheduled(
      fixedDelayString = "${retwitt.cleaning.fixedRateMs}",
      initialDelayString = "${retwitt.cleaning.initialDelayMs}")
  public void removeUnused() {
    retwittRepository
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<Retwitt>(Retwitt.class),
            new DocRemovingErrorHandler<Retwitt>(Retwitt.class));
  }

  private void persistRetwitts() {
    LOGGER.info("Starting persisting retwitts");
    twittsSource
        .twittsFlux()
        .flatMap(this::toRetwittDoc)
        .map(retwittRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<Retwitt>(Retwitt.class),
            new DocPersistingErrorHandler<Retwitt>(Retwitt.class));
  }

  private Mono<RetwittDoc> toRetwittDoc(Status status) {
    return just(status)
        .map(Status::getRetweetedStatus)
        .filter(Objects::nonNull)
        .filter(s -> s.getUser() != null)
        .map(this::toDoc);
  }

  private List<Retwitt> toDtos(List<RetwittDoc> docs) {
    return docs.stream().map(retwittMapper::docToDto).collect(toList());
  }

  private RetwittDoc toDoc(Status status) {
    return new RetwittDoc(
        status.getId(), status.getUser().getScreenName(), status.getRetweetCount(), utcNow());
  }
}
