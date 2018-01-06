package ab.java.twittertrends.domain.twitter.hashtag.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.ImmutableHashtag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HashtagRepository implements Repository<Hashtag> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);
	
	private static final String COUNT_LABEL = "count";
	private static final String NAME_LABEL = "name";

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Hashtag>> take(int count) {
		LOGGER.info("Getting {} hashtags", count);
		return reactiveMongoTemplate.findAll(HashtagDoc.class)
				.sort(Comparator.<HashtagDoc>comparingLong(t -> t.getCount()).reversed())
				.map(doc -> (Hashtag) ImmutableHashtag.builder()
						.name(doc.getName())
						.count(doc.getCount())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Hashtag hashtag) {
		LOGGER.info("Saving / updating {}", hashtag);
		
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(NAME_LABEL).is(hashtag.name())),
				Update.update(NAME_LABEL, hashtag.name())
					.set(HashtagDoc.LAST_UPDATE_LABEL, utcNow())
					.inc(COUNT_LABEL, hashtag.count().intValue()), 
				HashtagDoc.HASHTAGS);
	}
	
	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing hashtags older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(HashtagDoc.LAST_UPDATE_LABEL).lte(utcNowMinus(amount, unit))), 
				HashtagDoc.HASHTAGS);
	}

}
