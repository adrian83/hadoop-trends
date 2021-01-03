package com.github.adrian83.trends.domain.favorite.storage;

import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.COLLECTION;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.COUNT;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.ID;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.TWEET_ID;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.UPDATED;
import static com.github.adrian83.trends.domain.favorite.model.FavoriteDoc.USERNAME;
import static java.util.Comparator.comparingLong;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.favorite.model.FavoriteDoc;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class FavoriteRepository implements Repository<FavoriteDoc> {

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(FavoriteDoc favoriteDoc) {
    log.info("Saving favorite {}", favoriteDoc);
    return reactiveMongoTemplate
        .upsert(generateFindByIdQuery(favoriteDoc), generateUpdateStmt(favoriteDoc), COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    log.info("Removing favorites older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(remgenerateFindOlderThanQuery(utcNowMinus(amount, unit)), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<FavoriteDoc>> top(int count) {
    log.warn("Getting {} favorities", count);
    return reactiveMongoTemplate
        .findAll(FavoriteDoc.class, COLLECTION)
        .sort(comparingLong(FavoriteDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query remgenerateFindOlderThanQuery(long olderThan) {
    return query(where(UPDATED).lte(olderThan));
  }

  private Query generateFindByIdQuery(FavoriteDoc favoriteDoc) {
    return query(where(ID).is(favoriteDoc.getId()));
  }

  private Update generateUpdateStmt(FavoriteDoc favoriteDoc) {
    return update(ID, favoriteDoc.getId())
        .set(TWEET_ID, favoriteDoc.getTweetId())
        .set(USERNAME, favoriteDoc.getUsername())
        .set(COUNT, favoriteDoc.getCount())
        .set(UPDATED, favoriteDoc.getUpdated());
  }
}
