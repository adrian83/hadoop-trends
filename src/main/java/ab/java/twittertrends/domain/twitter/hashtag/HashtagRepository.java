package ab.java.twittertrends.domain.twitter.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.common.Observables;
import reactor.core.publisher.Flux;
import rx.Observable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HashtagRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashtagProcessor.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public void save(List<Hashtag> hashtags) {
		System.out.println(hashtags);
		
		LOGGER.debug("Saving / updating {} hashtags", hashtags.size());

		hashtags.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("name").is(t.name())),
						Update.update("name", t.name()).inc("count", t.count().intValue()), "hashtags"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}

	public Observable<List<HashtagDoc>> popularHashtags(int count) {
	
		LOGGER.debug("Fetch {} most popular hashtags", count);

		Flux<List<HashtagDoc>> flux = reactiveMongoTemplate
		.findAll(HashtagDoc.class)
				.sort(Comparator.<HashtagDoc>comparingLong(t -> t.getCount()).reversed())
				.buffer(count);
				
				return Observables.fromFlux(flux);
	}
	
}
