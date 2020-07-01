package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

public class DocPersistingSuccessHandler<T> implements Consumer<Mono<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocPersistingSuccessHandler.class);

  private Class<T> clazz;

  public DocPersistingSuccessHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Mono<String> idMono) {
    idMono.subscribe(id -> LOGGER.info("{} with id: {} persisted", clazz.getSimpleName(), id));
  }
}
