package com.github.adrian83.trends.domain.reply.storage;

import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.COLLECTION;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.REPLY_COUNT;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.TWEET_ID;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.UPDATED;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.USERNAME;
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
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.mongodb.client.result.DeleteResult;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ReplyRepository implements Repository<ReplyDoc> {

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(ReplyDoc replyDoc) {
    log.info("Saving reply {}", replyDoc);
    return reactiveMongoTemplate
        .upsert(generateFindByIdQuery(replyDoc), generateUpdateStmt(replyDoc), COLLECTION)
        .map(this::upsertedId);
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    log.info("Removing replies older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(generateFindOlderThanQuery(utcNowMinus(amount, unit)), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<ReplyDoc>> top(int count) {
    log.info("Getting {} replies", count);
    return reactiveMongoTemplate
        .findAll(ReplyDoc.class, COLLECTION)
        .sort(comparingLong(ReplyDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query generateFindOlderThanQuery(long olderThan) {
    return query(where(UPDATED).lte(olderThan));
  }

  private Query generateFindByIdQuery(ReplyDoc replyDoc) {
    return query(where(ReplyDoc.ID).is(replyDoc.getId()));
  }

  private Update generateUpdateStmt(ReplyDoc replyDoc) {
    return update(TWEET_ID, replyDoc.getTweetId())
        .set(USERNAME, replyDoc.getUsername())
        .inc(REPLY_COUNT, replyDoc.getCount())
        .set(UPDATED, replyDoc.getUpdated());
  }
}
