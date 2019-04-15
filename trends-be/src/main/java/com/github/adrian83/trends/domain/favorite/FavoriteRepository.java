package com.github.adrian83.trends.domain.favorite;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.github.adrian83.trends.common.Repository;
import com.github.adrian83.trends.common.Time;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository implements Repository<FavoriteDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Mono<UpdateResult> save(FavoriteDoc twitt) {
		LOGGER.info("Saving favorite {}", twitt);
		return reactiveMongoTemplate.upsert(Query.query(Criteria.where(FavoriteDoc.TWITT_ID).is(twitt.getTwittId())),
				Update.update(FavoriteDoc.TWITT_ID, twitt.getTwittId())
					.set(FavoriteDoc.USERNAME, twitt.getUsername())
					.set(FavoriteDoc.UPDATED, twitt.getUpdated()),
				FavoriteDoc.COLLECTION);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing favorites older than {} {}", amount, unit);
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(FavoriteDoc.UPDATED).lte(Time.utcNowMinus(amount, unit))), FavoriteDoc.COLLECTION);
	}
	
	public Flux<List<FavoriteDoc>> top(int count) {
		LOGGER.info("Getting {} favorities", count);
		return reactiveMongoTemplate.findAll(FavoriteDoc.class, FavoriteDoc.COLLECTION)
				.sort(Comparator.<FavoriteDoc>comparingLong(FavoriteDoc::getCount).reversed()).buffer(count).take(1)
				.onBackpressureDrop();
	}
	
}
