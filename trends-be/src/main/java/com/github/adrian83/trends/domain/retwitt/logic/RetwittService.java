package com.github.adrian83.trends.domain.retwitt.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Mono.empty;
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
    LOGGER.info("Created");
    persistRetwitts();
    LOGGER.info("Persisting retwitts initiated");
  }

  @Override
  public Flux<List<Retwitt>> top() {
	    LOGGER.info("Reading most retwitted twitts");
	    ConnectableFlux<List<Retwitt>> retwitted =
	        Flux.interval(Duration.ofSeconds(readIntervalSec))
	            .flatMap(i -> retwittRepository.top(readCount))
	            .map(list -> list.stream().map(retwittMapper::docToDto).collect(toList()))
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
            createRemoveSuccessConsumer(Retwitt.class, LOGGER),
            createRemoveErrorConsumer(Retwitt.class, LOGGER));
  }

  private void persistRetwitts() {
    LOGGER.info("Starting persisting retwitts");
    twittsSource
        .twittsFlux()
        .flatMap(this::toRetwittDoc)
        .map(retwittRepository::save)
        .subscribe(
            createPersistSuccessConsumer(Retwitt.class, LOGGER),
            createPersistErrorConsumer(Retwitt.class, LOGGER));
  }

  private Mono<RetwittDoc> toRetwittDoc(Status status) {
    var retwittStatus = status.getRetweetedStatus();
    if (retwittStatus == null || retwittStatus.getUser() == null) {
      return empty();
    }

    var doc =
        new RetwittDoc(
            retwittStatus.getId(),
            retwittStatus.getUser().getScreenName(),
            retwittStatus.getRetweetCount(),
            utcNow());
    return just(doc);
  }

}
