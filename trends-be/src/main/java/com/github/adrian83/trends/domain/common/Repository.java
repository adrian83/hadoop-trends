package com.github.adrian83.trends.domain.common;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.bson.BsonValue;

import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository<T> {

  Mono<String> save(T elem);

  Mono<Long> deleteOlderThan(long amount, TimeUnit unit);

  Flux<List<T>> top(int amount);

  default String upsertedId(UpdateResult updateResult) {
    return Optional.ofNullable(updateResult)
        .map(UpdateResult::getUpsertedId)
        .map(BsonValue::toString)
        .orElse("unknown");
  }
}
