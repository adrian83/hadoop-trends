package ab.java.twittertrends.domain.twitter.favorite.repository;

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

import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.ImmutableFavorite;
import ab.java.twittertrends.domain.twitter.hashtag.repository.HashtagRepository;
import reactor.core.publisher.Flux;

@Component
public class FavoriteRepository {

	private static final Logger LOGGER = Logger.getLogger(HashtagRepository.class.getSimpleName());

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public void save(List<Favorite> favorites) {

		LOGGER.log(Level.INFO,"Saving / updating {0} favorites", favorites.size());

		favorites.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(
						Query.query(Criteria.where("twittId").is(t.id())),
						Update.update("twittId", t.id()).set("favorite", t.favorite()).set("user", t.user()), 
						"favorites"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}

	public Flux<List<Favorite>> mostFavorited(int count) {

		LOGGER.log(Level.INFO, "Getting {0} favorites", count);

		Flux<List<Favorite>> flux = reactiveMongoTemplate.findAll(FavoriteDoc.class)
				.sort(Comparator.<FavoriteDoc>comparingLong(FavoriteDoc::getFavorite).reversed())
				.map(doc -> (Favorite)ImmutableFavorite.builder()
						.id(doc.getTwittId())
						.favorite(doc.getFavorite())
						.user(doc.getUser())
						.build())
				.buffer(count)
				.take(1)
				.onBackpressureDrop();

		return flux;
	}

}
