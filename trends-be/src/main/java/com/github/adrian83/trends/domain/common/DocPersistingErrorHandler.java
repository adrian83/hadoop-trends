package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocPersistingErrorHandler<T> implements Consumer<Throwable> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocRemovingErrorHandler.class);

  private Class<T> docClass;

  public DocPersistingErrorHandler(Class<T> docClass) {
    super();
    this.docClass = docClass;
  }

  @Override
  public void accept(Throwable fault) {
    fault.printStackTrace();
    LOGGER.error("Exception during processing {} {}", docClass.getSimpleName(), fault.getMessage());
  }
}
