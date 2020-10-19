package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocRemovingErrorHandler<T> implements Consumer<Throwable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocRemovingErrorHandler.class);

  private Class<T> clazz;

  public DocRemovingErrorHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Throwable fault) {
    LOGGER.error(
        "Exception while removing instances of {}: {}",
        clazz.getSimpleName(),
        fault == null ? "null" : fault.getMessage());
  }
}
