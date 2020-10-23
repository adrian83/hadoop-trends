package com.github.adrian83.trends.domain.common;

import java.util.List;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface StatusFetcher<T> {

  Flux<List<T>> fetch(int size, int seconds);
}
