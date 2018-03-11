package ab.java.twittertrends.domain.twitter.retwitt;

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

import ab.java.twittertrends.common.Time;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetwittRepository implements Repository<Retwitt>{

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Retwitt>> take(int count) {
		LOGGER.info("Getting {} retwitts", count);
		
		return reactiveMongoTemplate.findAll(Retwitt.class, Retwitt.RETWITTS)
				.sort(Comparator.<Retwitt>comparingLong(Retwitt::getCount).reversed())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Retwitt retwitt) {
		LOGGER.info("Saving / updating {}", retwitt);
		
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(Retwitt.TWITT_ID_LABEL).is(retwitt.getTwittId())), 
				Update.update(Retwitt.TWITT_ID_LABEL, retwitt.getTwittId())
					.set(Retwitt.COUNT_LABEL, retwitt.getCount())
					.set(Retwitt.USER_LABEL, retwitt.getUserName())
					.set(Retwitt.LAST_UPDATE_LABEL, retwitt.getUpdated()), 
					Retwitt.RETWITTS);
	}
	
	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing retwitts older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(Retwitt.LAST_UPDATE_LABEL).lte(Time.utcNowMinus(amount, unit))), 
				Retwitt.RETWITTS);
	}
	
}
