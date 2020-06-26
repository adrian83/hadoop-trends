package com.github.adrian83.trends.domain.common;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocRemovingSuccessHandler<T> implements Consumer<Long> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocRemovingErrorHandler.class);

  private Class<T> docClass;

  public DocRemovingSuccessHandler(Class<T> docClass) {
    super();
    this.docClass = docClass;
  }

  @Override
  public void accept(Long count) {
	  LOGGER.warn("Removed {} instances of {}", count, docClass.getSimpleName());
  }
}
