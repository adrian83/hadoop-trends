package com.github.adrian83.trends.domain.twitt;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.github.adrian83.trends.common.Repository;
import com.github.adrian83.trends.common.Time;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

public class TwittRepository implements Repository<TwittDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwittRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Mono<UpdateResult> save(TwittDoc twitt) {
		LOGGER.info("Saving twitt {}", twitt);
		return reactiveMongoTemplate.upsert(Query.query(Criteria.where(TwittDoc.TWITT_ID).is(twitt.getTwittId())),
				Update.update(TwittDoc.TWITT_ID, twitt.getTwittId()).set(TwittDoc.USERNAME, twitt.getUsername())
						.set(TwittDoc.FAVORITE_COUNT, twitt.getFavoriteCount())
						.set(TwittDoc.REPLY_COUNT, twitt.getReplyCount())
						.set(TwittDoc.RETWITT_COUNT, twitt.getRetwittCount()).set(TwittDoc.UPDATED, twitt.getUpdated()),
				TwittDoc.COLLECTION);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing twitts older than {} {}", amount, unit);
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(TwittDoc.UPDATED).lte(Time.utcNowMinus(amount, unit))),
				TwittDoc.COLLECTION);
	}

}
