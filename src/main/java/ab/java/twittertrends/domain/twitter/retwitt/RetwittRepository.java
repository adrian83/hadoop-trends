package ab.java.twittertrends.domain.twitter.retwitt;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.common.Observables;
import reactor.core.publisher.Flux;
import rx.Observable;



@Component
public class RetwittRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	
	public void save(List<Retwitt> retwitts) {
		System.out.println(retwitts);
		
		LOGGER.debug("Saving / updating {} retwitts", retwitts.size());

		retwitts.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(t.id())),
						Update.update("twittId", t.id()).set("retwitted", t.retwitted()), "retwitts"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}
	
	public Observable<List<RetwittDoc>> popularTwitts(int count) {
		
		LOGGER.debug("Fetch {} most popular twitts", count);

		Flux<List<RetwittDoc>> flux = reactiveMongoTemplate
		.findAll(RetwittDoc.class)
				.sort(Comparator.<RetwittDoc>comparingLong(t -> t.getRetwitted()).reversed())
				.buffer(count);
				
				return Observables.fromFlux(flux);
	}
	
	
}
