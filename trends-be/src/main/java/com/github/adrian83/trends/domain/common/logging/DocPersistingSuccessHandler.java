package com.github.adrian83.trends.domain.common.logging;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class DocPersistingSuccessHandler<T> implements Consumer<Mono<String>> {

  private Class<T> clazz;

  public DocPersistingSuccessHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Mono<String> idMono) {
    idMono.subscribe(id -> log.info("{} with id: {} persisted", clazz.getSimpleName(), id));
  }
}
