package com.github.adrian83.trends.domain.common;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Service<T> {

  void removeUnused();

  Flux<List<T>> top();

  default Consumer<Mono<String>> createPersistSuccessConsumer(Class<T> clazz, Logger logger) {
    return (Mono<String> idMono) ->
        idMono.subscribe(id -> logger.info("{} with id: {} persisted", clazz.getSimpleName(), id));
  }

  default Consumer<Throwable> createPersistErrorConsumer(Class<T> clazz, Logger logger) {
    return (Throwable fault) ->
        logger.error("Exception during processing {} {}", clazz.getSimpleName(), fault);
  }

  default Consumer<Long> createRemoveSuccessConsumer(Class<T> clazz, Logger logger) {
    return (Long count) -> logger.warn("Removed {} instances of {}", count, clazz.getSimpleName());
  }

  default Consumer<Throwable> createRemoveErrorConsumer(Class<T> clazz, Logger logger) {
    return (Throwable fault) ->
        logger.error("Exception while removing instances of {}: {}", clazz.getSimpleName(), fault);
  }
}
