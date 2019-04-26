package com.github.adrian83.trends.domain.retwitt.logic;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.retwitt.model.Retwitt;
import com.github.adrian83.trends.domain.retwitt.model.RetwittDoc;
import com.github.adrian83.trends.domain.retwitt.model.RetwittMapper;
import com.github.adrian83.trends.domain.retwitt.storage.RetwittRepository;
import com.github.adrian83.trends.domain.status.StatusSource;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

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

  private ConnectableFlux<List<Retwitt>> retwitted;

  @PostConstruct
  public void postCreate() {
    LOGGER.info("Created");
    persistRetwitts();
    readRetwitts();
    LOGGER.info("Reading and persisting retwitts initiated");
  }

  @Override
  public Flux<List<Retwitt>> top() {
    return retwitted;
  }

  @Override
  @Scheduled(
      fixedDelayString = "${retwitt.cleaning.fixedRateMs}",
      initialDelayString = "${retwitt.cleaning.initialDelayMs}")
  public void removeUnused() {
    retwittRepository
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(REMOVE_SUCCESS_CONSUMER, REMOVE_ERROR_CONSUMER);
  }

  private void persistRetwitts() {
    LOGGER.info("Starting persisting retwitts");
    twittsSource
        .twittsFlux()
        .flatMap(this::toRetwittDoc)
        .map(retwittRepository::save)
        .subscribe(PERSIST_SUCCESS_CONSUMER, PERSIST_ERROR_CONSUMER);
  }

  private Mono<RetwittDoc> toRetwittDoc(Status status) {
    Status retwittStatus = status.getRetweetedStatus();
    if (retwittStatus == null || retwittStatus.getUser() == null) {
      return Mono.empty();
    }

    RetwittDoc doc =
        new RetwittDoc(
            retwittStatus.getId(),
            retwittStatus.getUser().getScreenName(),
            retwittStatus.getRetweetCount(),
            Time.utcNow());
    return Mono.just(doc);
  }

  private void readRetwitts() {
    LOGGER.info("Reading most retwitted twitts");
    retwitted =
        Flux.interval(Duration.ofSeconds(readIntervalSec))
            .flatMap(i -> retwittRepository.top(readCount))
            .map(list -> list.stream().map(retwittMapper::docToDto).collect(Collectors.toList()))
            .publish();
    retwitted.connect();
  }

  private static final Consumer<Mono<UpdateResult>> PERSIST_SUCCESS_CONSUMER =
      (Mono<UpdateResult> updateResult) ->
          updateResult.subscribe(ur -> LOGGER.info("Retwitt updated: {}", ur));

  private static final Consumer<Throwable> PERSIST_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during processing retwitts {}", fault);

  private static final Consumer<DeleteResult> REMOVE_SUCCESS_CONSUMER =
      (DeleteResult deleteResult) -> LOGGER.warn("Retwitts removed: {}", deleteResult);

  private static final Consumer<Throwable> REMOVE_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during removing retwitts {}", fault);
}
