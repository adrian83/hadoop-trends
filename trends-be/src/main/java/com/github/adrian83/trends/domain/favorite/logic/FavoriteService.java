package com.github.adrian83.trends.domain.favorite.logic;

import static com.github.adrian83.trends.common.Time.utcNow;
import static java.lang.Long.valueOf;
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

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.common.StatusCleaner;
import com.github.adrian83.trends.domain.common.StatusFetcher;
import com.github.adrian83.trends.domain.common.StatusProcessor;
import com.github.adrian83.trends.domain.common.logging.DocPersistingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocPersistingSuccessHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingErrorHandler;
import com.github.adrian83.trends.domain.common.logging.DocRemovingSuccessHandler;
import com.github.adrian83.trends.domain.favorite.model.Favorite;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.github.adrian83.trends.domain.favorite.model.FavoriteMapper;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Service
public class FavoriteService implements StatusProcessor, StatusCleaner, StatusFetcher<Favorite> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteService.class);

  private Repository<FavoriteDoc> favoriteRepository;
  private FavoriteMapper favoriteMapper;

  @Autowired
  public FavoriteService(
      Repository<FavoriteDoc> favoriteRepository, FavoriteMapper favoriteMapper) {
    this.favoriteRepository = favoriteRepository;
    this.favoriteMapper = favoriteMapper;
  }

  @Override
  public void processStatusses(Flux<Status> statusses) {
    LOGGER.info("Persisting favorites initiated");
    statusses
        .flatMap(this::toFavorite)
        .map(favoriteRepository::save)
        .subscribe(
            new DocPersistingSuccessHandler<>(Favorite.class),
            new DocPersistingErrorHandler<>(Favorite.class));
  }

  @Override
  public void removeOlderThanSec(int seconds) {
    favoriteRepository
        .deleteOlderThan(seconds, SECONDS)
        .subscribe(
            new DocRemovingSuccessHandler<>(Favorite.class),
            new DocRemovingErrorHandler<>(Favorite.class));
  }

  @Override
  public Flux<List<Favorite>> fetch(int size, int seconds) {
    LOGGER.info("Reading most favorited twitts");
    ConnectableFlux<List<Favorite>> favorited =
        Flux.interval(ofSeconds(seconds))
            .flatMap(i -> favoriteRepository.top(size))
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
    return FavoriteDoc.builder()
    		.id(Long.toString(s.getId()))
    		.username(s.getUser().getScreenName())
    		.count(valueOf(s.getFavoriteCount())).updated(utcNow())
    		.build();
  }

  private List<Favorite> toDtos(List<FavoriteDoc> docs) {
    return docs.stream().map(favoriteMapper::docToDto).collect(toList());
  }
}
