package com.github.adrian83.trends.domain.common.logging;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DocRemovingSuccessHandler<T> implements Consumer<Long> {

  private Class<T> clazz;

  public DocRemovingSuccessHandler(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  @Override
  public void accept(Long count) {
    log.warn("Removed {} instances of {}", count, clazz.getSimpleName());
  }
}
