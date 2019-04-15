package com.github.adrian83.trends.domain.hashtag;

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

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HashtagRepository implements Repository<HashtagDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);
	
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;



	@Override
	public Mono<UpdateResult> save(HashtagDoc hashtag) {
		LOGGER.info("Saving / updating {}", hashtag);
		
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(HashtagDoc.NAME).is(hashtag.getName())),
				Update.update(HashtagDoc.NAME, hashtag.getName())
					.set(HashtagDoc.UPDATED, hashtag.getUpdated())
					.inc(HashtagDoc.OCCURRENCE_COUNT, hashtag.getOccurrenceCount()), 
					HashtagDoc.COLLECTION);
	}
	
	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing hashtags older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(HashtagDoc.UPDATED).lte(Time.utcNowMinus(amount, unit))), 
				HashtagDoc.COLLECTION);
	}
	
	public Flux<List<HashtagDoc>> top(int count) {
		LOGGER.info("Getting {} hashtags", count);
		return reactiveMongoTemplate.findAll(HashtagDoc.class, HashtagDoc.COLLECTION)
				.sort(Comparator.<HashtagDoc>comparingLong(HashtagDoc::getOccurrenceCount).reversed()).buffer(count).take(1)
				.onBackpressureDrop();
	}

}
