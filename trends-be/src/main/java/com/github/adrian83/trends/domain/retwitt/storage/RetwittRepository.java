package com.github.adrian83.trends.domain.retwitt.storage;

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
            Query.query(Criteria.where(RetwittDoc.TWITT_ID).is(twitt.getTwittId())),
            Update.update(RetwittDoc.TWITT_ID, twitt.getTwittId())
                .set(RetwittDoc.USERNAME, twitt.getUsername())
                .set(RetwittDoc.RETWITT_COUNT, twitt.getCount())
                .set(RetwittDoc.UPDATED, twitt.getUpdated()),
            RetwittDoc.COLLECTION)
        .map((ur) -> ur.getUpsertedId().asString().getValue());
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing twitts older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(
            Query.query(Criteria.where(RetwittDoc.UPDATED).lte(Time.utcNowMinus(amount, unit))),
            RetwittDoc.COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<List<RetwittDoc>> top(int count) {
    LOGGER.info("Getting {} retwitts", count);
    return reactiveMongoTemplate
        .findAll(RetwittDoc.class, RetwittDoc.COLLECTION)
        .sort(Comparator.<RetwittDoc>comparingLong(RetwittDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }
}
