package com.github.adrian83.trends.domain.reply.storage;

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
        .upsert(saveQuery(replyDoc), saveUpdate(replyDoc), ReplyDoc.COLLECTION)
        .map((ur) -> ur.getUpsertedId().asString().getValue());
  }

  @Override
  public Mono<Long> deleteOlderThan(long amount, TimeUnit unit) {
    LOGGER.info("Removing replies older than {} {}", amount, unit);
    return reactiveMongoTemplate
        .remove(removeQuery(Time.utcNowMinus(amount, unit)), ReplyDoc.COLLECTION)
        .map(DeleteResult::getDeletedCount);
  }

  @Override
  public Flux<List<ReplyDoc>> top(int count) {
    LOGGER.info("Getting {} replies", count);
    return reactiveMongoTemplate
        .findAll(ReplyDoc.class, ReplyDoc.COLLECTION)
        .sort(Comparator.<ReplyDoc>comparingLong(ReplyDoc::getCount).reversed())
        .buffer(count)
        .take(1)
        .onBackpressureDrop();
  }

  private Query removeQuery(long olderThan) {
    return Query.query(Criteria.where(ReplyDoc.UPDATED).lte(olderThan));
  }

  private Query saveQuery(ReplyDoc replyDoc) {
    return Query.query(Criteria.where(ReplyDoc.TWITT_ID).is(replyDoc.getTwittId()));
  }

  private Update saveUpdate(ReplyDoc replyDoc) {
    return Update.update(ReplyDoc.TWITT_ID, replyDoc.getTwittId())
        .set(ReplyDoc.USERNAME, replyDoc.getUsername())
        .inc(ReplyDoc.REPLY_COUNT, replyDoc.getCount())
        .set(ReplyDoc.UPDATED, replyDoc.getUpdated());
  }
}
