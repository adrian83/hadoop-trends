package com.github.adrian83.trends.domain.favorite.storage;

import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.COLLECTION;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.COUNT;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.TWITT_ID;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.UPDATED;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.USERNAME;
import static java.util.Comparator.comparingLong;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository implements Repository<FavoriteDoc> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteRepository.class);

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(FavoriteDoc favoriteDoc) {
    LOGGER.info("Saving favorite {}", favoriteDoc);
    return reactiveMongoTemplate
        .upsert(saveQuery(favoriteDoc), saveUpdate(favoriteDoc), COLLECTION)
        .map((ur) -> ur.getUpsertedId().asString().getValue());
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing favorites older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(removeQuery(utcNowMinus(amount, unit)), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<FavoriteDoc>> top(int count) {
    LOGGER.warn("Getting {} favorities", count);
    return reactiveMongoTemplate
        .findAll(FavoriteDoc.class, COLLECTION)
        .sort(comparingLong(FavoriteDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query removeQuery(long olderThan) {
    return query(where(UPDATED).lte(olderThan));
  }

  private Query saveQuery(FavoriteDoc favoriteDoc) {
    return query(where(TWITT_ID).is(favoriteDoc.getTwittId()));
  }

  private Update saveUpdate(FavoriteDoc favoriteDoc) {
    return update(TWITT_ID, favoriteDoc.getTwittId())
        .set(USERNAME, favoriteDoc.getUsername())
        .set(COUNT, favoriteDoc.getCount())
        .set(UPDATED, favoriteDoc.getUpdated());
  }
}
