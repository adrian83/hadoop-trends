package ab.java.twittertrends.domain.twitter.retwitt.repository;

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

import ab.java.twittertrends.domain.twitter.retwitt.ImmutableRetwitt;
import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RetwittRepository {

	private static final Logger LOGGER = Logger.getLogger(RetwittRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	public Mono<UpdateResult> saveSingle(Retwitt retwitt) {
		LOGGER.log(Level.INFO, "Saving / updating {0}", retwitt);
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where("twittId").is(retwitt.id())), 
				Update.update("twittId", retwitt.id()).set("retwitted", retwitt.retwitted()).set("user", retwitt.user()), "retwitts");
	}
	
	public Flux<List<Retwitt>> mostRetwitted(int count) {
		LOGGER.log(Level.INFO, "Getting {0} retwitts", count);
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
	
	
}
