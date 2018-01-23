package ab.java.twittertrends.domain.twitter.hashtag;

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
import ab.java.twittertrends.domain.twitter.common.Time;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HashtagRepository implements Repository<Hashtag> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);
	
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Hashtag>> take(int count) {
		LOGGER.info("Getting {} hashtags", count);
		return reactiveMongoTemplate.findAll(Hashtag.class, Hashtag.HASHTAGS)
				.sort(Comparator.<Hashtag>comparingLong(Hashtag::getCount).reversed())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Hashtag hashtag) {
		LOGGER.info("Saving / updating {}", hashtag);
		
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(Hashtag.NAME_LABEL).is(hashtag.getName())),
				Update.update(Hashtag.NAME_LABEL, hashtag.getName())
					.set(Hashtag.LAST_UPDATE_LABEL, hashtag.getUpdated())
					.inc(Hashtag.COUNT_LABEL, hashtag.getCount()), 
				Hashtag.HASHTAGS);
	}
	
	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing hashtags older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(Hashtag.LAST_UPDATE_LABEL).lte(Time.utcNowMinus(amount, unit))), 
				Hashtag.HASHTAGS);
	}

}
