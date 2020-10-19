package com.github.adrian83.trends.domain.hashtag.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Consumer;

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
import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.common.Service;
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

  private static final Consumer<Throwable> DOC_REMOVING_ERROR_HANDLER =
      new DocRemovingErrorHandler<>(Hashtag.class);
  private static final Consumer<Long> DOC_REMOVING_SUCCESS_HANDLER =
      new DocRemovingSuccessHandler<>(Hashtag.class);
  private static final Consumer<Throwable> DOC_PERSISTING_ERROR_HANDLER =
      new DocPersistingErrorHandler<>(Hashtag.class);
  private static final Consumer<Mono<String>> DOC_PERSISTING_SUCCESS_HANDLER =
      new DocPersistingSuccessHandler<>(Hashtag.class);

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
  private int cleaningIntervalSec;

  @PostConstruct
  public void postCreate() {
    persistHashtags();
    LOGGER.info("Persisting hashtags initiated");
  }

  @Override
  public Flux<List<Hashtag>> top() {
    LOGGER.info("Reading most popular hashtags");
    ConnectableFlux<List<Hashtag>> hashtags =
        Flux.interval(ofSeconds(readIntervalSec))
            .flatMap(i -> hashtagRepository.top(readCount))
            .map(this::toDtos)
            .publish();
    hashtags.connect();
    return hashtags;
  }

  @Override
  @Scheduled(
      fixedDelayString = "${hashtag.cleaning.fixedRateMs}",
      initialDelayString = "${hashtag.cleaning.initialDelayMs}")
  public void removeUnused() {
    hashtagRepository
        .deleteOlderThan(cleaningIntervalSec, SECONDS)
        .subscribe(DOC_REMOVING_SUCCESS_HANDLER, DOC_REMOVING_ERROR_HANDLER);
  }

  private void persistHashtags() {
    LOGGER.info("Starting persisting hashtags");
    twittsSource
        .twittsFlux()
        .map(Status::getText)
        .flatMap(hashtagFinder::findHashtags)
        .buffer(DEF_BUFFER_SIZE)
        .flatMapIterable(this::toHashtagDoc)
        .map(hashtagRepository::save)
        .subscribe(DOC_PERSISTING_SUCCESS_HANDLER, DOC_PERSISTING_ERROR_HANDLER);
  }

  private List<HashtagDoc> toHashtagDoc(List<String> hashtags) {
    return hashtags
        .stream()
        .collect(groupingBy(identity(), counting()))
        .entrySet()
        .stream()
        .map(e -> new HashtagDoc(e.getKey(), e.getValue().intValue(), utcNow()))
        .collect(toList());
  }

  private List<Hashtag> toDtos(List<HashtagDoc> docs) {
    return docs.stream().map(hashtagMapper::docToDto).collect(toList());
  }
}
