package ab.java.twittertrends.domain.twitter.favorite;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ab.java.twittertrends.domain.twitter.TwittsSource;
import ab.java.twittertrends.domain.twitter.favorite.repository.FavoriteRepository;

import reactor.core.publisher.Mono;
import twitter4j.Status;

@Component
public class FavoriteProcessor {

	private static final Logger LOGGER = Logger.getLogger(FavoriteProcessor.class.getSimpleName());
	
	@Autowired
	private TwittsSource twittsSource;
	
	@Autowired
	private FavoriteRepository favoriteRepository;
	
	
	@PostConstruct
	public void postCreate() {
		LOGGER.log(Level.INFO, "FavoriteProcessor created");
		persistFavorities();
	}
	
	private boolean shouldProcess(Status status) {
		return status.getRetweetedStatus() != null 
				&& status.getRetweetedStatus().getFavoriteCount() > 0
				&& status.getRetweetedStatus().getId() > 0
				&& status.getRetweetedStatus().getUser() != null
				&& status.getRetweetedStatus().getUser().getScreenName() != null;
	}
	
	private void persistFavorities() {
		LOGGER.log(Level.INFO, "Starting persisting favorites");
	
		twittsSource.twittsFlux()
		.filter(this::shouldProcess)
		.map(s -> (Favorite) ImmutableFavorite.builder()
				.id(String.valueOf(s.getRetweetedStatus().getId()))
				.favorite(s.getRetweetedStatus().getFavoriteCount())
				.user(s.getRetweetedStatus().getUser().getScreenName())
				.build())
		.map(favoriteRepository::saveSingle)
		.map(Mono::block)
		.subscribe(ur -> LOGGER.log(Level.INFO, "Saved favorite: {0}", ur.getUpsertedId()));        
	}

}
