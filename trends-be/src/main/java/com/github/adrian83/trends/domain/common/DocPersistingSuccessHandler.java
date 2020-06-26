package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

public class DocPersistingSuccessHandler<T> implements Consumer<Mono<String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocRemovingErrorHandler.class);

  private Class<T> docClass;

  public DocPersistingSuccessHandler(Class<T> docClass) {
    super();
    this.docClass = docClass;
  }

  @Override
  public void accept(Mono<String> idMono) {
    idMono.subscribe(id -> LOGGER.info("{} with id: {} persisted", docClass.getSimpleName(), id));
  }
}
