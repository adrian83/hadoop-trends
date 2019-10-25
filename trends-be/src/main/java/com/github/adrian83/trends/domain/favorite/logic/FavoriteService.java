package com.github.adrian83.trends.domain.favorite.logic;

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

  @Autowired private Repository<FavoriteDoc> favoriteRepository;
  @Autowired private FavoriteMapper favoriteMapper;
  @Autowired private StatusSource twittsSource;

  @Value("${favorite.read.intervalSec}")
  private int readIntervalSec;

  @Value("${favorite.read.count}")
  private int readCount;

  @Value("${favorite.cleaning.olderThanSec}")
  private int olderThanSec;


  @PostConstruct
  public void postCreate() {
    persistFavorites();
    LOGGER.info("Persisting favorites initiated");
  }

  @Override
  public Flux<List<Favorite>> top() {
	    LOGGER.info("Reading most favorited twitts");
	    ConnectableFlux<List<Favorite>> favorited =
	        Flux.interval(Duration.ofSeconds(readIntervalSec))
	            .flatMap(i -> favoriteRepository.top(readCount))
	            .map(list -> list.stream().map(favoriteMapper::docToDto).collect(toList()))
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
        .deleteOlderThan(olderThanSec, TimeUnit.SECONDS)
        .subscribe(
            createRemoveSuccessConsumer(Favorite.class, LOGGER),
            createRemoveErrorConsumer(Favorite.class, LOGGER));
  }

  private void persistFavorites() {
    LOGGER.info("Starting persisting favorites");
    twittsSource
        .twittsFlux()
        .flatMap(this::toFavorite)
        .map(favoriteRepository::save)
        .subscribe(
            createPersistSuccessConsumer(Favorite.class, LOGGER),
            createPersistErrorConsumer(Favorite.class, LOGGER));
  }

  private Mono<FavoriteDoc> toFavorite(Status status) {
    var retweetedStatus = status.getRetweetedStatus();
    if (retweetedStatus == null
        || retweetedStatus.getFavoriteCount() < 0
        || retweetedStatus.getId() < 0
        || retweetedStatus.getUser() == null
        || retweetedStatus.getUser().getScreenName() == null) {
      return empty();
    }

    var favorite =
        new FavoriteDoc(
            retweetedStatus.getId(),
            retweetedStatus.getUser().getScreenName(),
            Long.valueOf(retweetedStatus.getFavoriteCount()),
            utcNow());

    return just(favorite);
  }
}
