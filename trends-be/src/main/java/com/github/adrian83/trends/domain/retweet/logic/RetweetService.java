package com.github.adrian83.trends.domain.retweet.logic;

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

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.common.logging.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.retweet.model.Retweet;
import com.github.adrian83.trends.domain.retweet.model.RetweetDoc;
import com.github.adrian83.trends.domain.retweet.model.RetweetMapper;
import com.github.adrian83.trends.domain.retweet.storage.RetweetRepository;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Service
public class RetweetService implements StatusProcessor, StatusCleaner, StatusFetcher<Retweet> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetweetService.class);

  private RetweetRepository retweetRepository;
  private RetweetMapper retweetMapper;

  @Autowired
  public RetweetService(RetweetRepository retweetRepository, RetweetMapper retweetMapper) {
    super();
    this.retweetRepository = retweetRepository;
    this.retweetMapper = retweetMapper;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting retweets initiated");
    statusses
        .flatMap(this::toRetweetDoc)
        .map(retweetRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<>(Retweet.class),
            new DocPersistingErrorHandler<>(Retweet.class));
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    retweetRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<>(Retweet.class),
            new DocRemovingErrorHandler<>(Retweet.class));
  }

  @Override
  public Flux<List<Retweet>> fetch(int size, int seconds) {
    LOGGER.info("Reading most retweeted tweets");
    ConnectableFlux<List<Retweet>> retweeted =
        Flux.interval(ofSeconds(seconds))
            .flatMap(i -> retweetRepository.top(size))
            .map(this::toDtos)
            .publish();
    retweeted.connect();
    return retweeted;
  }

  private Mono<RetweetDoc> toRetweetDoc(Status status) {
    return justOrEmpty(status)
        .filter(s -> nonNull(s.getRetweetedStatus()))
        .map(Status::getRetweetedStatus)
        .filter(s -> nonNull(s.getUser()))
        .filter(s -> nonNull(s.getUser().getScreenName()))
        .map(this::toDoc);
  }

  private RetweetDoc toDoc(Status s) {
    var tweetId = Long.toString(s.getId());
    return RetweetDoc.builder()
        .id(tweetId)
        .tweetId(tweetId)
        .username(s.getUser().getScreenName())
        .count(s.getRetweetCount())
        .updated(Time.utcNow())
        .build();
  }

  private List<Retweet> toDtos(List<RetweetDoc> docs) {
    return docs.stream().map(retweetMapper::docToDto).collect(toList());
  }
}
