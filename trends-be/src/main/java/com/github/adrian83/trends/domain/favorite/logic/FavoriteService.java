package com.github.adrian83.trends.domain.favorite.logic;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;
import com.github.adrian83.trends.domain.favorite.storage.FavoriteRepository;
import com.github.adrian83.trends.domain.status.StatusSource;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@org.springframework.stereotype.Service
public class FavoriteService implements Service<Favorite> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

  @Autowired private FavoriteRepository favoriteRepository;
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private StatusSource twittsSource;

  private ConnectableFlux<List<Favorite>> favorited;

  @PostConstruct
  public void postCreate() {
    LOGGER.info("Created");
    persistFavorites();
    readFavorites();
    LOGGER.info("Reading and persisting favorites initiated");
  }

  @Override
  public Flux<List<Favorite>> top() {
    return favorited;
  }

  @Override
  @Scheduled(fixedRate = CLEANING_FIXED_RATE_MS, initialDelay = CLEANING_INITIAL_DELAY_MS)
  public void removeUnused() {
    Mono<DeleteResult> result = favoriteRepository.deleteOlderThan(1, TimeUnit.MINUTES);
    result.subscribe(REMOVE_SUCCESS_CONSUMER, REMOVE_ERROR_CONSUMER);
  }

  private void readFavorites() {
    LOGGER.info("Reading most favorited twitts");
    favorited =
        Flux.interval(Duration.ofSeconds(10))
            .flatMap(i -> favoriteRepository.top(10))
            .map(list -> list.stream().map(favoriteMapper::docToDto).collect(Collectors.toList()))
            .publish();
    favorited.connect();
  }

  private void persistFavorites() {
    LOGGER.info("Starting persisting favorites");
    twittsSource
        .twittsFlux()
        .flatMap(this::toFavorite)
        .map(favoriteRepository::save)
        .subscribe(PERSIST_SUCCESS_CONSUMER, PERSIST_ERROR_CONSUMER);
  }

  private Mono<FavoriteDoc> toFavorite(Status status) {
    Status retweetedStatus = status.getRetweetedStatus();
    if (retweetedStatus == null
        || retweetedStatus.getFavoriteCount() < 0
        || retweetedStatus.getId() < 0
        || retweetedStatus.getUser() == null
        || retweetedStatus.getUser().getScreenName() == null) {
      return Mono.empty();
    }

    FavoriteDoc favorite =
        new FavoriteDoc(
            null,
            retweetedStatus.getId(),
            retweetedStatus.getUser().getScreenName(),
            Long.valueOf(retweetedStatus.getFavoriteCount()),
            Time.utcNow());

    return Mono.just(favorite);
  }

  private static final Consumer<Mono<UpdateResult>> PERSIST_SUCCESS_CONSUMER =
      (Mono<UpdateResult> updateResult) -> LOGGER.info("Favorite updated: {}", updateResult);

  private static final Consumer<Throwable> PERSIST_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during processing favorites {}", fault);

  private static final Consumer<DeleteResult> REMOVE_SUCCESS_CONSUMER =
      (DeleteResult deleteResult) -> LOGGER.info("Favorites removed: {}", deleteResult);

  private static final Consumer<Throwable> REMOVE_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during removeing favorites {}", fault);
}
