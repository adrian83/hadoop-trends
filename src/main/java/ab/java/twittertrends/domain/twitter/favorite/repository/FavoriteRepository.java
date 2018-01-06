package ab.java.twittertrends.domain.twitter.favorite.repository;

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

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.ImmutableFavorite;
import ab.java.twittertrends.domain.twitter.retwitt.repository.RetwittRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository implements Repository<Favorite> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RetwittRepository.class);

	private static final String USER_LABEL = "user";
	private static final String FAVORITE_LABEL = "favorite";
	private static final String TWITT_ID_LABEL = "twittId";

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Favorite>> take(int count) {
		LOGGER.info("Getting {} favorites", count);
		return reactiveMongoTemplate.findAll(FavoriteDoc.class)
				.sort(Comparator.<FavoriteDoc>comparingLong(FavoriteDoc::getFavorite).reversed())
				.map(doc -> (Favorite)ImmutableFavorite.builder()
						.id(doc.getTwittId())
						.favorite(doc.getFavorite())
						.user(doc.getUser())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();
	}

	@Override
	public Mono<UpdateResult> save(Favorite favorite) {
		LOGGER.info("Saving favorite {}", favorite);
		 return reactiveMongoTemplate.upsert(
				 Query.query(Criteria.where(TWITT_ID_LABEL).is(favorite.id())), 
				 Update.update(TWITT_ID_LABEL, favorite.id())
				 	.set(FAVORITE_LABEL, favorite.favorite())
				 	.set(FavoriteDoc.LAST_UPDATE_LABEL, utcNow())
				 	.set(USER_LABEL, favorite.user()), 
				 FavoriteDoc.FAVORITES);
	}

	@Override
	public Mono<DeleteResult> deleteOlderThan(long amount, TimeUnit unit) {
		LOGGER.info("Removing favorities older than {} {}", amount, unit);
		
		return reactiveMongoTemplate.remove(
				Query.query(Criteria.where(FavoriteDoc.LAST_UPDATE_LABEL).lte(utcNowMinus(amount, unit))), 
				FavoriteDoc.FAVORITES);
	}
	
}
