package com.github.adrian83.trends.domain.favorite.storage;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
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
        .upsert(saveQuery(favoriteDoc), saveUpdate(favoriteDoc), FavoriteDoc.COLLECTION)
        .map((ur) -> ur.getUpsertedId().asString().getValue());
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing favorites older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(removeQuery(Time.utcNowMinus(amount, unit)), FavoriteDoc.COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<FavoriteDoc>> top(int count) {
    LOGGER.warn("Getting {} favorities", count);
    return reactiveMongoTemplate
        .findAll(FavoriteDoc.class, FavoriteDoc.COLLECTION)
        .sort(Comparator.<FavoriteDoc>comparingLong(FavoriteDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query removeQuery(long olderThan) {
    return Query.query(Criteria.where(FavoriteDoc.UPDATED).lte(olderThan));
  }

  private Query saveQuery(FavoriteDoc favoriteDoc) {
    return Query.query(Criteria.where(FavoriteDoc.TWITT_ID).is(favoriteDoc.getTwittId()));
  }

  private Update saveUpdate(FavoriteDoc favoriteDoc) {
    return Update.update(FavoriteDoc.TWITT_ID, favoriteDoc.getTwittId())
        .set(FavoriteDoc.USERNAME, favoriteDoc.getUsername())
        .set(FavoriteDoc.COUNT, favoriteDoc.getCount())
        .set(FavoriteDoc.UPDATED, favoriteDoc.getUpdated());
  }
}
