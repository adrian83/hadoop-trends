package ab.java.twittertrends.domain.twitter.hashtag.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.ImmutableHashtag;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class HashtagRepository {

	private static final Logger LOGGER = Logger.getLogger(HashtagRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public void save(List<Hashtag> hashtags) {

		LOGGER.log(Level.INFO, "Saving / updating {0} hashtags", hashtags.size());

		hashtags.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(
						Query.query(Criteria.where("name").is(t.name())),
						Update.update("name", t.name()).inc("count", t.count().intValue()), 
						"hashtags"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}

	public Flux<List<Hashtag>> popularHashtags(int count) {

		LOGGER.log(Level.INFO, "Getting {0} hashtags", count);

		Flux<List<Hashtag>> flux = reactiveMongoTemplate.findAll(HashtagDoc.class)
				.sort(Comparator.<HashtagDoc>comparingLong(t -> t.getCount()).reversed())
				.map(doc -> (Hashtag) ImmutableHashtag.builder()
						.name(doc.getName())
						.count(doc.getCount())
						.build())
				.buffer(count).take(1);

		return flux.onBackpressureDrop();
	}

}
