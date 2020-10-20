package com.github.adrian83.trends.domain.common;

import reactor.core.publisher.Flux;
import twitter4j.Status;

@FunctionalInterface
public interface StatusProcessor {

  void processStatusses(Flux<Status> statuses);
}
