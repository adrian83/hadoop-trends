package ab.java.twittertrends.domain.twitter.favorite;

import javax.annotation.PostConstruct;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.UpdateResult;

import ab.java.twittertrends.common.Time;
import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.common.Repository;
import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteProcessor.class);

	@Autowired
	private TwittsSource twittsSource;

	@Autowired
	private Repository<Favorite> favoriteRepository;

	@PostConstruct
	public void postCreate() {
		LOGGER.info("FavoriteProcessor created");
		persistFavorities();
	}

	private void persistFavorities() {
		LOGGER.info("Starting persisting favorites");

		twittsSource.twittsFlux()
			.flatMap(this::toFavorite)
			.map(favoriteRepository::save)
			.subscribe(this::onUpdate);
	}

	private Publisher<Favorite> toFavorite(Status status) {
		Status retweetedStatus = status.getRetweetedStatus();
		if (retweetedStatus != null && 
				retweetedStatus.getFavoriteCount() > 0 && 
				retweetedStatus.getId() > 0 && 
				retweetedStatus.getUser() != null && 
				retweetedStatus.getUser().getScreenName() != null) {
			
			Favorite favorite = Favorite.builder()
					.twittId(String.valueOf(status.getRetweetedStatus().getId()))
					.count(status.getRetweetedStatus().getFavoriteCount())
					.userName(status.getRetweetedStatus().getUser().getScreenName())
					.updated(Time.utcNow()).build();
			
			return Mono.just(favorite);
		}
		return Mono.empty();
	}

	private void onUpdate(Mono<UpdateResult> updateResult) {
		updateResult.subscribe(ur -> LOGGER.info("Saved favorite: {}", ur.getUpsertedId()));
	}

}
