package com.github.adrian83.trends.domain.reply.storage;

import static com.github.adrian83.trends.common.Time.utcNowMinus;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.COLLECTION;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.REPLY_COUNT;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.TWITT_ID;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.UPDATED;
import static com.github.adrian83.trends.domain.reply.model.ReplyDoc.USERNAME;
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
import com.github.adrian83.trends.domain.reply.model.ReplyDoc;
import com.mongodb.client.result.DeleteResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReplyRepository implements Repository<ReplyDoc> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReplyRepository.class);

  @Autowired private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Mono<String> save(ReplyDoc replyDoc) {
    LOGGER.info("Saving reply {}", replyDoc);
    return reactiveMongoTemplate
        .upsert(saveQuery(replyDoc), saveUpdate(replyDoc), COLLECTION)
        .map(this::upsertedId); 
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing replies older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(removeQuery(utcNowMinus(amount, unit)), COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<ReplyDoc>> top(int count) {
    LOGGER.info("Getting {} replies", count);
    return reactiveMongoTemplate
        .findAll(ReplyDoc.class, COLLECTION)
        .sort(comparingLong(ReplyDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query removeQuery(long olderThan) {
    return query(where(UPDATED).lte(olderThan));
  }

  private Query saveQuery(ReplyDoc replyDoc) {
    return query(where(ReplyDoc.ID).is(replyDoc.getId()));
  }

  private Update saveUpdate(ReplyDoc replyDoc) {
    return update(TWITT_ID, replyDoc.getTwittId())
        .set(USERNAME, replyDoc.getUsername())
        .inc(REPLY_COUNT, replyDoc.getCount())
        .set(UPDATED, replyDoc.getUpdated());
  }
}
