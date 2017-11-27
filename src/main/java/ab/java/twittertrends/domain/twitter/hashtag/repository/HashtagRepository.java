package ab.java.twittertrends.domain.twitter.hashtag.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.hashtag.Hashtag;
import ab.java.twittertrends.domain.twitter.hashtag.ImmutableHashtag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class HashtagRepository implements Repository<Hashtag> {

	private static final String COUNT_LABEL = "count";
	private static final String NAME_LABEL = "name";

	private static final Logger LOGGER = Logger.getLogger(HashtagRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Hashtag>> take(int count) {
		LOGGER.log(Level.INFO, "Getting {0} hashtags", count);
		return reactiveMongoTemplate.findAll(HashtagDoc.class)
				.sort(Comparator.<HashtagDoc>comparingLong(t -> t.getCount()).reversed())
				.map(doc -> (Hashtag) ImmutableHashtag.builder()
						.name(doc.getName())
						.count(doc.getCount())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Hashtag hashtag) {
		LOGGER.log(Level.INFO, "Saving / updating {0}", hashtag);
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(NAME_LABEL).is(hashtag.name())),
				Update.update(NAME_LABEL, hashtag.name()).inc(COUNT_LABEL, hashtag.count().intValue()), 
				HashtagDoc.HASHTAGS);
	}

}
