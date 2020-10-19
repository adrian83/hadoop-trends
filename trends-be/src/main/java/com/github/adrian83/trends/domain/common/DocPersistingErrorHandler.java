package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocPersistingErrorHandler<T> implements Consumer<Throwable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocPersistingErrorHandler.class);

  private Class<T> clazz;

  public DocPersistingErrorHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Throwable fault) {
    LOGGER.error(
        "Exception during processing {} {}",
        clazz.getSimpleName(),
        fault == null ? "null" : fault.getMessage());
  }
}
