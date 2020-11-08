package com.github.adrian83.trends.domain.retweet.storage;

import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.ID;
import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.COLLECTION;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.RETWEET_COUNT;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.TWITT_ID;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.UPDATED;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.USERNAME;
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
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.retweet.model.RetweetDoc;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetweetRepository implements Repository<RetweetDoc> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetweetRepository.class);

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(RetweetDoc twitt) {
    LOGGER.info("Saving twitt {}", twitt);
    return reactiveMongoTemplate
        .upsert(
            query(where(ID).is(twitt.getTwittId())),
            update(TWITT_ID, twitt.getTwittId())
                .set(USERNAME, twitt.getUsername())
                .set(RETWEET_COUNT, twitt.getCount())
                .set(UPDATED, twitt.getUpdated()),
            COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing twitts older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(
            query(where(UPDATED).lte(utcNowMinus(amount, unit))),
            COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<List<RetweetDoc>> top(int count) {
    LOGGER.info("Getting {} retweets", count);
    return reactiveMongoTemplate
        .findAll(RetweetDoc.class, COLLECTION)
        .sort(comparingLong(RetweetDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }
}
