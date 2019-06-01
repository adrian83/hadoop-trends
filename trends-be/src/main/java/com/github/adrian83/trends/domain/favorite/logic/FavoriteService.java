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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.github.adrian83.trends.common.Repository;
import com.github.adrian83.trends.common.Service;
import com.github.adrian83.trends.common.Time;
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

  @Autowired private Repository<FavoriteDoc> favoriteRepository;
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private StatusSource twittsSource;

  @Value("${favorite.read.intervalSec}")
  private int readIntervalSec;

  @Value("${favorite.read.count}")
  private int readCount;

  @Value("${favorite.cleaning.olderThanSec}")
  private int olderThanSec;

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
  @Scheduled(
      fixedDelayString = "${favorite.cleaning.fixedRateMs}",
      initialDelayString = "${favorite.cleaning.initialDelayMs}")
  public void removeUnused() {
    favoriteRepository
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(REMOVE_SUCCESS_CONSUMER, REMOVE_ERROR_CONSUMER);
  }

  private void readFavorites() {
    LOGGER.info("Reading most favorited twitts");
    favorited =
        Flux.interval(Duration.ofSeconds(readIntervalSec))
            .flatMap(i -> favoriteRepository.top(readCount))
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
            retweetedStatus.getId(),
            retweetedStatus.getUser().getScreenName(),
            Long.valueOf(retweetedStatus.getFavoriteCount()),
            Time.utcNow());

    return Mono.just(favorite);
  }

  private static final Consumer<Mono<String>> PERSIST_SUCCESS_CONSUMER =
      (Mono<String> idMono) -> idMono.subscribe(id -> LOGGER.info("Favorite {} persisted", id));

  private static final Consumer<Throwable> PERSIST_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during processing favorites {}", fault);

  private static final Consumer<Long> REMOVE_SUCCESS_CONSUMER =
      (Long count) -> LOGGER.warn("Removed {} Favorites", count);

  private static final Consumer<Throwable> REMOVE_ERROR_CONSUMER =
      (Throwable fault) -> LOGGER.error("Exception during removeing favorites {}", fault);
}
