package ab.java.twittertrends.domain.twitter.reply;

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
public class ReplyRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplyRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;
	
	
	public void save(List<Reply> replies) {
		System.out.println(replies);
		
		LOGGER.debug("Saving / updating {} replies", replies.size());

		replies.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(t.id())),
				Update.update("twittId", t.id())
				.set("user", t.user())
				.inc("count", t.count().intValue()), "replies"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}
	
	public Observable<List<ReplyDoc>> replies(int count) {
		
		LOGGER.debug("Fetch {} replies", count);

		Flux<List<ReplyDoc>> flux = reactiveMongoTemplate
		.findAll(ReplyDoc.class)
				.sort(Comparator.<ReplyDoc>comparingLong(t -> t.getCount()).reversed())
				.buffer(count).take(1);
				
				return Observables.fromFlux(flux);
	}

}
