package com.github.adrian83.trends.domain.hashtag.storage;

import static com.github.adrian83.trends.domain.hashtag.model.HashtagDoc.COLLECTION;
import static com.github.adrian83.trends.domain.hashtag.model.HashtagDoc.COUNT;
import static com.github.adrian83.trends.domain.hashtag.model.HashtagDoc.NAME;
import static com.github.adrian83.trends.domain.hashtag.model.HashtagDoc.UPDATED;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.hashtag.model.HashtagDoc;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class HashtagRepository implements Repository<HashtagDoc> {

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(HashtagDoc hashtag) {
    log.info("Saving / updating {}", hashtag);

    return reactiveMongoTemplate
        .upsert(
            query(where(NAME).is(hashtag.getName())),
            update(NAME, hashtag.getName())
                .set(UPDATED, hashtag.getUpdated())
                .inc(COUNT, hashtag.getCount()),
            COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    log.info("Removing hashtags older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(query(where(UPDATED).lte(Time.utcNowMinus(amount, unit))), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<List<HashtagDoc>> top(int count) {
    log.info("Getting {} hashtags", count);
    return reactiveMongoTemplate
        .findAll(HashtagDoc.class, COLLECTION)
        .sort(Comparator.<HashtagDoc>comparingLong(HashtagDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }
}
