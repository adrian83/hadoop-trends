package com.github.adrian83.trends.domain.favorite.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.lang.Long.valueOf;
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

import com.github.adrian83.trends.domain.common.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.common.Service;
import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@org.springframework.stereotype.Service
public class FavoriteService implements Service<Favorite>, StatusProcessor, StatusCleaner {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

  private static final Consumer<Throwable> DOC_REMOVING_ERROR_HANDLER =
      new DocRemovingErrorHandler<>(Favorite.class);
  private static final Consumer<Long> DOC_REMOVING_SUCCESS_HANDLER =
      new DocRemovingSuccessHandler<>(Favorite.class);
  private static final Consumer<Throwable> DOC_PERSISTING_ERROR_HANDLER =
      new DocPersistingErrorHandler<>(Favorite.class);
  private static final Consumer<Mono<String>> DOC_PERSISTING_SUCCESS_HANDLER =
      new DocPersistingSuccessHandler<>(Favorite.class);

  private Repository<FavoriteDoc> favoriteRepository;
  private FavoriteMapper favoriteMapper;

  private int readIntervalSec;
  private int readCount;

  @Autowired
  public FavoriteService(
      Repository<FavoriteDoc> favoriteRepository,
      FavoriteMapper favoriteMapper,
      @Value("${favorite.read.intervalSec}") int readIntervalSec,
      @Value("${favorite.read.count}") int readCount) {
    this.favoriteRepository = favoriteRepository;
    this.favoriteMapper = favoriteMapper;
    this.readIntervalSec = readIntervalSec;
    this.readCount = readCount;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting favorites initiated");
    statusses
        .flatMap(this::toFavorite)
        .map(favoriteRepository::save)
        .subscribe(DOC_PERSISTING_SUCCESS_HANDLER, DOC_PERSISTING_ERROR_HANDLER);
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    favoriteRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(DOC_REMOVING_SUCCESS_HANDLER, DOC_REMOVING_ERROR_HANDLER);
  }

  @Override
  public Flux<List<Favorite>> top() {
    LOGGER.info("Reading most favorited twitts");
    ConnectableFlux<List<Favorite>> favorited =
        Flux.interval(ofSeconds(readIntervalSec))
            .flatMap(i -> favoriteRepository.top(readCount))
            .map(this::toDtos)
            .publish();
    favorited.connect();
    return favorited;
  }

  private Mono<FavoriteDoc> toFavorite(Status status) {
    return justOrEmpty(status)
        .filter(s -> nonNull(s.getRetweetedStatus()))
        .map(Status::getRetweetedStatus)
        .filter(s -> s.getFavoriteCount() >= 0)
        .filter(s -> s.getId() >= 0)
        .filter(s -> nonNull(s.getUser()))
        .filter(s -> nonNull(s.getUser().getScreenName()))
        .map(this::toDoc);
  }

  private FavoriteDoc toDoc(Status s) {
    return new FavoriteDoc(
        s.getId(), s.getUser().getScreenName(), valueOf(s.getFavoriteCount()), utcNow());
  }

  private List<Favorite> toDtos(List<FavoriteDoc> docs) {
    return docs.stream().map(favoriteMapper::docToDto).collect(toList());
  }
}
