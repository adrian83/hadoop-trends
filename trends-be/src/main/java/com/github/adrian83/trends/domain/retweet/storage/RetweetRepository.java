package com.github.adrian83.trends.domain.retweet.storage;

import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.COLLECTION;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.ID;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.RETWEET_COUNT;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.TWEET_ID;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.UPDATED;
import static com.github.adrian83.trends.domain.retweet.model.RetweetDoc.USERNAME;
import static java.util.Comparator.comparingLong;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.domain.common.Repository;
import com.github.adrian83.trends.domain.retweet.model.RetweetDoc;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RetweetRepository implements Repository<RetweetDoc> {

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(RetweetDoc tweet) {
    log.info("Saving tweet {}", tweet);
    return reactiveMongoTemplate
        .upsert(
            query(where(ID).is(tweet.getTweetId())),
            update(TWEET_ID, tweet.getTweetId())
                .set(USERNAME, tweet.getUsername())
                .set(RETWEET_COUNT, tweet.getCount())
                .set(UPDATED, tweet.getUpdated()),
            COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    log.info("Removing tweets older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(query(where(UPDATED).lte(utcNowMinus(amount, unit))), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  public Flux<List<RetweetDoc>> top(int count) {
    log.info("Getting {} retweets", count);
    return reactiveMongoTemplate
        .findAll(RetweetDoc.class, COLLECTION)
        .sort(comparingLong(RetweetDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }
}
