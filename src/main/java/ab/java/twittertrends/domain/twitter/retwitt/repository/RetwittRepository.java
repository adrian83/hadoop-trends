package ab.java.twittertrends.domain.twitter.retwitt.repository;

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


import ab.java.twittertrends.domain.twitter.retwitt.ImmutableRetwitt;
import ab.java.twittertrends.domain.twitter.retwitt.Retwitt;
import reactor.core.publisher.Flux;

@Component
public class RetwittRepository {

	private static final Logger LOGGER = Logger.getLogger(RetwittRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	
	public void save(List<Retwitt> retwitts) {
		
		LOGGER.log(Level.INFO, "Saving / updating {0} retwitts", retwitts.size());

		retwitts.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(t.id())),
						Update.update("twittId", t.id()).set("retwitted", t.retwitted()), "retwitts"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}
	
	public Flux<List<Retwitt>> mostRetwitted(int count) {
		
		LOGGER.log(Level.INFO, "Getting {0} retwitts", count);

		Flux<List<Retwitt>> flux = reactiveMongoTemplate.findAll(RetwittDoc.class)
				.sort(Comparator.<RetwittDoc>comparingLong(t -> t.getRetwitted()).reversed())
				.map(doc -> (Retwitt) ImmutableRetwitt.builder()
						.id(doc.getTwittId())
						.retwitted(doc.getRetwitted())
						.build())
				.buffer(count)
				.take(1);
				
		return flux.onBackpressureDrop();
	}
	
	
}
