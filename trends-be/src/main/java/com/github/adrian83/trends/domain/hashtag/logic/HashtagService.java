package com.github.adrian83.trends.domain.hashtag.logic;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Repository;
import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.hashtag.model.Hashtag;
import com.github.adrian83.trends.domain.hashtag.model.HashtagDoc;
import com.github.adrian83.trends.domain.hashtag.model.HashtagMapper;
import com.github.adrian83.trends.domain.status.StatusSource;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class HashtagService implements Service<Hashtag> {

  private static final Logger LOGGER = LoggerFactory.getLogger(HashtagService.class);

  private static final int DEF_BUFFER_SIZE = 100;

  @Autowired private Repository<HashtagDoc> hashtagRepository;
  @Autowired private StatusSource twittsSource;
  @Autowired private HashtagFinder hashtagFinder;
  @Autowired private HashtagMapper hashtagMapper;

  @Value("${hashtag.read.intervalSec}")
  private int readIntervalSec;

  @Value("${hashtag.read.count}")
  private int readCount;

  @Value("${hashtag.cleaning.olderThanSec}")
  private int olderThanSec;

  private ConnectableFlux<List<Hashtag>> hashtags;

  @PostConstruct
  public void postCreate() {
    LOGGER.info("Created");
    persistHashtags();
    readHashtags();
    LOGGER.info("Reading and persisting hashtags initiated");
  }

  @Override
  public Flux<List<Hashtag>> top() {
    return hashtags;
  }

  @Override
  @Scheduled(
      fixedDelayString = "${hashtag.cleaning.fixedRateMs}",
      initialDelayString = "${hashtag.cleaning.initialDelayMs}")
  public void removeUnused() {
    hashtagRepository
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(REMOVE_SUCCESS_CONSUMER, REMOVE_ERROR_CONSUMER);
  }

  private void readHashtags() {
    LOGGER.info("Reading most popular hashtags");
    hashtags =
        Flux.interval(Duration.ofSeconds(readIntervalSec))
            .flatMap(i -> hashtagRepository.top(readCount).map(this::toDtos))
            .publish();
    hashtags.connect();
  }

  private void persistHashtags() {
    LOGGER.info("Starting persisting hashtags");
    twittsSource
        .twittsFlux()
        .map(Status::getText)
        .flatMap(hashtagFinder::findHashtags)
        .buffer(DEF_BUFFER_SIZE)
        .flatMapIterable(this::toDocuments)
        .map(hashtagRepository::save)
        .subscribe(PERSIST_SUCCESS_CONSUMER, PERSIST_ERROR_CONSUMER);
  }

  private List<HashtagDoc> toDocuments(List<String> hashtags) {
    return hashtags
        .stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .map(e -> new HashtagDoc(e.getKey(), e.getValue().intValue(), Time.utcNow()))
        .collect(Collectors.toList());
  }

  private List<Hashtag> toDtos(List<HashtagDoc> docs) {
    return docs.stream().map(hashtagMapper::docToDto).collect(Collectors.toList());
  }

  private static final Consumer<Mono<String>> PERSIST_SUCCESS_CONSUMER =
      (Mono<String> idMono) -> idMono.subscribe(id -> LOGGER.info("Hashtag {} persisted", id));

  private static final Consumer<Throwable> PERSIST_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during processing hashtags {}", fault);

  private static final Consumer<Long> REMOVE_SUCCESS_CONSUMER =
      (Long count) -> LOGGER.warn("Removed {} Hashtags", count);

  private static final Consumer<Throwable> REMOVE_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during removeing hashtags {}", fault);
}
