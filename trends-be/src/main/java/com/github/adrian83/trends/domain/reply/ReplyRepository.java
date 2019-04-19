package com.github.adrian83.trends.domain.reply;

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
public class ReplyRepository implements Repository<ReplyDoc> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Mono<UpdateResult> save(ReplyDoc twitt) {
		LOGGER.info("Saving reply {}", twitt);
		return reactiveMongoTemplate.upsert(Query.query(Criteria.where(ReplyDoc.TWITT_ID).is(twitt.getTwittId())),
				Update.update(ReplyDoc.TWITT_ID, twitt.getTwittId())
					.set(ReplyDoc.USERNAME, twitt.getUsername())
					.inc(ReplyDoc.REPLY_COUNT, twitt.getCount())
					.set(ReplyDoc.UPDATED, twitt.getUpdated()),
				ReplyDoc.COLLECTION);
		
	}
	

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing replies older than {} {}", amount, unit);
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(ReplyDoc.UPDATED).lte(Time.utcNowMinus(amount, unit))), ReplyDoc.COLLECTION);
	}
	
	@Override
	public Flux<List<ReplyDoc>> top(int count) {
		LOGGER.info("Getting {} replies", count);
		return reactiveMongoTemplate
				.findAll(ReplyDoc.class, ReplyDoc.COLLECTION)
				.sort(Comparator.<ReplyDoc>comparingLong(ReplyDoc::getCount).reversed())
				.buffer(count)
				 //.map(l -> {LOGGER.warn("1 DOCS {}", l); return l;})
				.take(1)
				.onBackpressureDrop();
	}
	
}

