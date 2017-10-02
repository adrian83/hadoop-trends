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

import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.ImmutableFavorite;
import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FavoriteRepository {

	private static final Logger LOGGER = Logger.getLogger(HashtagRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public Mono<UpdateResult> saveSingle(Favorite favorite) {
		LOGGER.log(Level.INFO,"Saving favorite {0}", favorite);
		 return reactiveMongoTemplate.upsert(
				 Query.query(Criteria.where("twittId").is(favorite.id())), 
				 Update.update("twittId", favorite.id())
				 	.set("favorite", favorite.favorite())
					.set("user", favorite.user()), "favorites");
	}
	
	public Flux<List<Favorite>> mostFavorited(int count) {
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

}
