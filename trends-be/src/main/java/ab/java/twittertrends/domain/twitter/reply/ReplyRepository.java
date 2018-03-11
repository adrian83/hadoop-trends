package ab.java.twittertrends.domain.twitter.reply;

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
import ab.java.twittertrends.domain.twitter.hashtag.HashtagRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class ReplyRepository implements Repository<Reply> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Reply>> take(int count) {
		LOGGER.info("Getting {} replies", count);
		return reactiveMongoTemplate.findAll(Reply.class, Reply.REPLIES)
				.sort(Comparator.<Reply>comparingLong(Reply::getCount).reversed())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Reply reply) {
		LOGGER.info("Saving / updating {}", reply);
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(Reply.TWITT_ID_LABEL).is(reply.getTwittId())), 
				Update.update(Reply.TWITT_ID_LABEL, reply.getTwittId())
					.set(Reply.USER_LABEL, reply.getUserName())
					.set(Reply.LAST_UPDATE_LABEL, reply.getUpdated())
					.inc(Reply.COUNT_LABEL, reply.getCount()), 
				Reply.REPLIES);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing replies older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(Reply.LAST_UPDATE_LABEL).lte(Time.utcNowMinus(amount, unit))), 
				Reply.REPLIES);
	}
	
}
