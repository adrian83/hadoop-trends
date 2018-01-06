package ab.java.twittertrends.domain.twitter.retwitt.repository;

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

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.retwitt.ImmutableRetwitt;
import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetwittRepository implements Repository<Retwitt>{

	private static final String USER_LABEL = "user";
	private static final String RETWITTED_LABEL = "retwitted";
	private static final String TWITT_ID_LABEL = "twittId";

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Retwitt>> take(int count) {
		LOGGER.info("Getting {} retwitts", count);
		
		return reactiveMongoTemplate.findAll(RetwittDoc.class)
				.sort(Comparator.<RetwittDoc>comparingLong(t -> t.getRetwitted()).reversed())
				.map(doc -> (Retwitt) ImmutableRetwitt.builder()
						.id(doc.getTwittId().toString())
						.retwitted(doc.getRetwitted())
						.user(doc.getUser())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Retwitt retwitt) {
		LOGGER.info("Saving / updating {}", retwitt);
		
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(TWITT_ID_LABEL).is(retwitt.id())), 
				Update.update(TWITT_ID_LABEL, retwitt.id())
					.set(RETWITTED_LABEL, retwitt.retwitted())
					.set(USER_LABEL, retwitt.user())
					.set(RetwittDoc.LAST_UPDATE_LABEL, utcNow()), 
				RetwittDoc.RETWITTS);
	}
	
	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing retwitts older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(RetwittDoc.LAST_UPDATE_LABEL).lte(utcNow()-5)), 
				RetwittDoc.RETWITTS);
	}
	
}
