package ab.java.twittertrends.domain.twitter.reply.repository;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.reply.ImmutableReply;
import ab.java.twittertrends.domain.twitter.reply.Reply;
import reactor.core.publisher.Flux;



@Component
public class ReplyRepository {

	private static final Logger LOGGER = Logger.getLogger(ReplyRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	
	public void save(List<Reply> replies) {
		
		LOGGER.log(Level.INFO, "Saving / updating {0} replies", replies.size());

		replies.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(t.id())),
				Update.update("twittId", t.id())
				.set("user", t.user())
				.inc("count", t.count().intValue()), "replies"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}
	
	public Flux<List<Reply>> mostReplied(int count) {
		
		LOGGER.log(Level.INFO, "Getting {0} replies", count);

		Flux<List<Reply>> flux = reactiveMongoTemplate.findAll(ReplyDoc.class)
				.sort(Comparator.<ReplyDoc>comparingLong(t -> t.getCount()).reversed())
				.map(doc -> (Reply) ImmutableReply.builder()
						.user(doc.getUser())
						.id(doc.getTwittId().toString())
						.count(doc.getCount())
						.build())
				.buffer(count)
				.take(1).onBackpressureDrop();
				
		return flux;
	}

}
