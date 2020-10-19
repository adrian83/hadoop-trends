package com.github.adrian83.trends.domain.retwitt.storage;

import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.ID;
import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.COLLECTION;
import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.RETWITT_COUNT;
import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.TWITT_ID;
import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.UPDATED;
import static com.github.adrian83.trends.domain.retwitt.model.RetwittDoc.USERNAME;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Time;
import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.retwitt.model.RetwittDoc;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetwittRepository implements Repository<RetwittDoc> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(RetwittDoc twitt) {
    LOGGER.info("Saving twitt {}", twitt);
    return reactiveMongoTemplate
        .upsert(
            query(where(ID).is(twitt.getTwittId())),
            update(TWITT_ID, twitt.getTwittId())
                .set(USERNAME, twitt.getUsername())
                .set(RETWITT_COUNT, twitt.getCount())
                .set(UPDATED, twitt.getUpdated()),
            COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing twitts older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(query(where(UPDATED).lte(Time.utcNowMinus(amount, unit))), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<List<RetwittDoc>> top(int count) {
    LOGGER.info("Getting {} retwitts", count);
    return reactiveMongoTemplate
        .findAll(RetwittDoc.class, COLLECTION)
        .sort(Comparator.<RetwittDoc>comparingLong(RetwittDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }
}
