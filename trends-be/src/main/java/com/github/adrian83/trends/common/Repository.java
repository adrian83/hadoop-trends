package com.github.adrian83.trends.common;

import java.util.List;
import java.util.concurrent.TimeUnit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository <T> {

	Mono<String> save(T elem);
	
	Mono<Long> deleteOlderThan(long amount, TimeUnit unit);
	
	Flux<List<T>> top(int amount);
	
}
