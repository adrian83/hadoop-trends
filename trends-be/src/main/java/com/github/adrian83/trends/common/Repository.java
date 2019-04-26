package com.github.adrian83.trends.common;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Repository <T> {

	Mono<UpdateResult> save(T elem);
	
	Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit);
	
	Flux<List<T>> top(int amount);
	
}
