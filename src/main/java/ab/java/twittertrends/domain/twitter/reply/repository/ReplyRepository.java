package ab.java.twittertrends.domain.twitter.reply.repository;

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

import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.reply.ImmutableReply;
import ab.java.twittertrends.domain.twitter.reply.Reply;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class ReplyRepository {

	private static final Logger LOGGER = Logger.getLogger(ReplyRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	public Mono<UpdateResult> saveSingle(Reply reply) {
		LOGGER.log(Level.INFO, "Saving / updating {0}", reply);
		return reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(reply.id())), Update.update("twittId", reply.id()).set("user", reply.user()).inc("count", reply.count().intValue()), "replies");
	}
	
	public Flux<List<Reply>> mostReplied(int count) {
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

}
