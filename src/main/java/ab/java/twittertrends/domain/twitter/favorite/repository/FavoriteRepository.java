package ab.java.twittertrends.domain.twitter.favorite.repository;

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

import ab.java.twittertrends.domain.twitter.common.Repository;
import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.ImmutableFavorite;
import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository implements Repository<Favorite> {

	private static final String USER_LABEL = "user";
	private static final String FAVORITE_LABEL = "favorite";
	private static final String TWITT_ID_LABEL = "twittId";

	private static final Logger LOGGER = Logger.getLogger(HashtagRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public Flux<List<Favorite>> take(int count) {
		LOGGER.log(Level.INFO, "Getting {0} favorites", count);
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
		LOGGER.log(Level.INFO,"Saving favorite {0}", favorite);
		 return reactiveMongoTemplate.upsert(
				 Query.query(Criteria.where(TWITT_ID_LABEL).is(favorite.id())), 
				 Update.update(TWITT_ID_LABEL, favorite.id()).set(FAVORITE_LABEL, favorite.favorite()).set(USER_LABEL, favorite.user()), 
				 FavoriteDoc.FAVORITES);
	}

}
