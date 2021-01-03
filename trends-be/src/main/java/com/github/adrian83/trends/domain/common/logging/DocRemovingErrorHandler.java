package com.github.adrian83.trends.domain.common.logging;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocRemovingErrorHandler<T> implements Consumer<Throwable> {

  private Class<T> clazz;

  public DocRemovingErrorHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Throwable fault) {
    log.error(
        "Exception while removing instances of {}: {}",
        clazz.getSimpleName(),
        fault == null ? "null" : fault.getMessage());
  }
}
