package ab.java.twittertrends.domain.twitter.favorite.repository;

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
import ab.java.twittertrends.domain.twitter.favorite.Favorite;
import ab.java.twittertrends.domain.twitter.favorite.ImmutableFavorite;
import reactor.core.publisher.Flux;
import rx.Observable;

@Component
public class FavoriteRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteRepository.class);

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	public void save(List<Favorite> favorites) {
		System.out.println(favorites);

		LOGGER.debug("Saving / updating {} favorites", favorites.size());

		favorites.stream()
				// it has to be changed to bulk operations in the near future
				.map(t -> reactiveMongoTemplate.upsert(Query.query(Criteria.where("twittId").is(t.id())),
						Update.update("twittId", t.id()).set("favorite", t.favorite()), "favorites"))
				.map(m -> m.block()) // TODO this need to be fixed
				.collect(Collectors.toList());
	}

	public Observable<List<Favorite>> favorites(int count) {

		LOGGER.debug("Fetch {} most popular favorites", count);

		Flux<List<Favorite>> flux = reactiveMongoTemplate.findAll(FavoriteDoc.class)
				.sort(Comparator.<FavoriteDoc>comparingLong(FavoriteDoc::getFaworite).reversed())
				.map(doc -> (Favorite)ImmutableFavorite.builder()
						.id(doc.getTwittId())
						.favorite(doc.getFaworite())
						.build())
				.buffer(count)
				.take(1);

		return Observables.fromFlux(flux);
	}

}
