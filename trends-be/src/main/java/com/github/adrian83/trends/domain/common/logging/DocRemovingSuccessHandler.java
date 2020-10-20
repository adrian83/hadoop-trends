package com.github.adrian83.trends.domain.common.logging;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocRemovingSuccessHandler<T> implements Consumer<Long> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocRemovingSuccessHandler.class);

  private Class<T> clazz;

  public DocRemovingSuccessHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Long count) {
    LOGGER.warn("Removed {} instances of {}", count, clazz.getSimpleName());
  }
}
