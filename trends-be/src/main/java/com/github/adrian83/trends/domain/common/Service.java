package com.github.adrian83.trends.domain.common;

import java.util.List;

import reactor.core.publisher.Flux;

public interface Service<T> {

  void removeUnused();

  Flux<List<T>> top();
}
