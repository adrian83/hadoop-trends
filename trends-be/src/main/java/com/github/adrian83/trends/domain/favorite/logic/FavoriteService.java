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

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.adrian83.trends.domain.common.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.common.Service;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.status.StatusSource;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@org.springframework.stereotype.Service
public class FavoriteService implements Service<Favorite> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

  private static final Consumer<Throwable> DOC_REMOVING_ERROR_HANDLER =
      new DocRemovingErrorHandler<>(Favorite.class);
  private static final Consumer<Long> DOC_REMOVING_SUCCESS_HANDLER =
      new DocRemovingSuccessHandler<>(Favorite.class);
  private static final Consumer<Throwable> DOC_PERSISTING_ERROR_HANDLER =
      new DocPersistingErrorHandler<>(Favorite.class);
  private static final Consumer<Mono<String>> DOC_PERSISTING_SUCCESS_HANDLER =
      new DocPersistingSuccessHandler<>(Favorite.class);

  @Autowired private Repository<FavoriteDoc> favoriteRepository;
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private StatusSource twittsSource;

  @Value("${favorite.read.intervalSec}")
  private int readIntervalSec;

  @Value("${favorite.read.count}")
  private int readCount;

  @Value("${favorite.cleaning.olderThanSec}")
  private int cleaningIntervalSec;

  @PostConstruct
  public void postCreate() {
    persistFavorites();
    LOGGER.info("Persisting favorites initiated");
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

  @Override
  @Scheduled(
      fixedDelayString = "${favorite.cleaning.fixedRateMs}",
      initialDelayString = "${favorite.cleaning.initialDelayMs}")
  public void removeUnused() {
    favoriteRepository
        .deleteOlderThan(cleaningIntervalSec, SECONDS)
        .subscribe(DOC_REMOVING_SUCCESS_HANDLER, DOC_REMOVING_ERROR_HANDLER);
  }

  private void persistFavorites() {
    LOGGER.info("Starting persisting favorites");
    twittsSource
        .twittsFlux()
        .flatMap(this::toFavorite)
        .map(favoriteRepository::save)
        .subscribe(DOC_PERSISTING_SUCCESS_HANDLER, DOC_PERSISTING_ERROR_HANDLER);
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
