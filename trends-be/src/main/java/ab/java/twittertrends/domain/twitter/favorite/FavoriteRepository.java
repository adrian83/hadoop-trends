package ab.java.twittertrends.domain.twitter.favorite;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.common.Time;
import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.retwitt.RetwittRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository implements Repository<Favorite> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Favorite>> take(int count) {
		LOGGER.info("Getting {} favorites", count);
		return reactiveMongoTemplate.findAll(Favorite.class, Favorite.FAVORITES)
				.sort(Comparator.<Favorite>comparingLong(Favorite::getCount).reversed()).buffer(count).take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Favorite favorite) {
		LOGGER.info("Saving favorite {}", favorite);
		return reactiveMongoTemplate.upsert(
				Query.query(Criteria.where(Favorite.TWITT_ID_LABEL).is(favorite.getTwittId())),
				Update.update(Favorite.TWITT_ID_LABEL, favorite.getTwittId())
						.set(Favorite.COUNT_LABEL, favorite.getCount())
						.set(Favorite.LAST_UPDATE_LABEL, favorite.getUpdated())
						.set(Favorite.USER_LABEL, favorite.getUserName()),
				Favorite.FAVORITES);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing favorities older than {} {}", amount, unit);
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(Favorite.LAST_UPDATE_LABEL).lte(Time.utcNowMinus(amount, unit))),
				Favorite.FAVORITES);
	}

}
