package com.github.adrian83.trends.domain.hashtag.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.common.logging.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.hashtag.model.Hashtag;
import com.github.adrian83.trends.domain.hashtag.model.HashtagDoc;
import com.github.adrian83.trends.domain.hashtag.model.HashtagMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import twitter4j.Status;

@Slf4j
@Component
public class HashtagService implements StatusProcessor, StatusCleaner, StatusFetcher<Hashtag> {

  private static final int DEF_BUFFER_SIZE = 100;

  private Repository<HashtagDoc> hashtagRepository;
  private HashtagFinder hashtagFinder;
  private HashtagMapper hashtagMapper;

  @Autowired
  public HashtagService(
      Repository<HashtagDoc> hashtagRepository,
      HashtagFinder hashtagFinder,
      HashtagMapper hashtagMapper) {
    super();
    this.hashtagRepository = hashtagRepository;
    this.hashtagFinder = hashtagFinder;
    this.hashtagMapper = hashtagMapper;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    log.info("Persisting hashtags initiated");
    statusses
        .map(Status::getText)
        .flatMap(hashtagFinder::findHashtags)
        .buffer(DEF_BUFFER_SIZE)
        .flatMapIterable(this::toHashtagDocs)
        .map(hashtagRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<>(Hashtag.class),
            new DocPersistingErrorHandler<>(Hashtag.class));
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    hashtagRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<>(Hashtag.class),
            new DocRemovingErrorHandler<>(Hashtag.class));
  }

  @Override
  public Flux<List<Hashtag>> fetch(int size, int seconds) {
    log.info("Reading most popular hashtags");
    ConnectableFlux<List<Hashtag>> hashtags =
        Flux.interval(ofSeconds(seconds))
            .flatMap(i -> hashtagRepository.top(size))
            .map(this::toDtos)
            .publish();
    hashtags.connect();
    return hashtags;
  }

  private List<HashtagDoc> toHashtagDocs(List<String> hashtags) {
    return hashtags
        .stream()
        .collect(groupingBy(identity(), counting()))
        .entrySet()
        .stream()
        .map(this::toDoc)
        .collect(toList());
  }

  private HashtagDoc toDoc(Entry<String, Long> e) {
    return HashtagDoc.builder()
        .id(e.getKey()).name(e.getKey())
        .count(e.getValue().intValue())
        .updated(utcNow())
        .build();
  }

  private List<Hashtag> toDtos(List<HashtagDoc> docs) {
    return docs.stream().map(hashtagMapper::docToDto).collect(toList());
  }
}
