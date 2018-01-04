package ab.java.twittertrends.domain.twitter.reply.repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.reply.ImmutableReply;
import ab.java.twittertrends.domain.twitter.reply.Reply;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class ReplyRepository implements Repository<Reply> {

	private static final String COUNT_LABEL = "count";
	private static final String USER_LABEL = "user";
	private static final String TWITT_ID_LABEL = "twittId";

	private static final Logger LOGGER = Logger.getLogger(ReplyRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Reply>> take(int count) {
		LOGGER.log(Level.INFO, "Getting {0} replies", count);
		return reactiveMongoTemplate.findAll(ReplyDoc.class)
				.sort(Comparator.<ReplyDoc>comparingLong(t -> t.getCount()).reversed())
				.map(doc -> (Reply) ImmutableReply.builder()
						.user(doc.getUser())
						.id(doc.getTwittId().toString())
						.count(doc.getCount())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Reply reply) {
		LOGGER.log(Level.INFO, "Saving / updating {0}", reply);
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(TWITT_ID_LABEL).is(reply.id())), 
				Update.update(TWITT_ID_LABEL, reply.id())
					.set(USER_LABEL, reply.user())
					.inc(COUNT_LABEL, reply.count().intValue()), 
				ReplyDoc.REPLIES);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(LocalDateTime time) {
		LOGGER.log(Level.INFO, "Removing replies older than {0}", time);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(ReplyDoc.LAST_UPDATE_LABEL).lte(time)), 
				ReplyDoc.REPLIES);
	}
	
}
